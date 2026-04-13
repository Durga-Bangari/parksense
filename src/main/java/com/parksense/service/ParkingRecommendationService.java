package com.parksense.service;

import com.parksense.exception.DestinationNotFoundException;
import com.parksense.geocoding.DestinationGeocodingProvider;
import com.parksense.model.DestinationRecommendationRequest;
import com.parksense.model.GeocodedDestination;
import com.parksense.model.Location;
import com.parksense.model.ParkingRecommendation;
import com.parksense.model.ParkingRecommendationRequest;
import com.parksense.model.ParkingRecommendationResponse;
import com.parksense.model.ParkingSpot;
import com.parksense.model.SearchHistory;
import com.parksense.provider.ParkingDataProvider;
import com.parksense.repository.SearchHistoryRepository;
import com.parksense.util.DistanceCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class ParkingRecommendationService {

    private final DestinationGeocodingProvider destinationGeocodingProvider;
    private final ParkingDataProvider parkingDataProvider;
    private final AvailabilityPredictionService availabilityPredictionService;
    private final PricePredictionService pricePredictionService;
    private final RecommendationScoringService recommendationScoringService;
    private final RecommendationExplanationService recommendationExplanationService;
    private final SearchHistoryRepository searchHistoryRepository;

    public ParkingRecommendationService(
            DestinationGeocodingProvider destinationGeocodingProvider,
            ParkingDataProvider parkingDataProvider,
            AvailabilityPredictionService availabilityPredictionService,
            PricePredictionService pricePredictionService,
            RecommendationScoringService recommendationScoringService,
            RecommendationExplanationService recommendationExplanationService,
            SearchHistoryRepository searchHistoryRepository
    ) {
        this.destinationGeocodingProvider = destinationGeocodingProvider;
        this.parkingDataProvider = parkingDataProvider;
        this.availabilityPredictionService = availabilityPredictionService;
        this.pricePredictionService = pricePredictionService;
        this.recommendationScoringService = recommendationScoringService;
        this.recommendationExplanationService = recommendationExplanationService;
        this.searchHistoryRepository = searchHistoryRepository;
    }

    public ParkingRecommendationResponse getRecommendations(ParkingRecommendationRequest request) {
        return getRecommendationsForLocation(
                new Location(request.latitude(), request.longitude()),
                request.arrivalTime()
        );
    }

    public ParkingRecommendationResponse getRecommendationsByDestination(
            DestinationRecommendationRequest request
    ) {
        GeocodedDestination geocodedDestination = destinationGeocodingProvider.geocode(request.destination())
                .orElseThrow(() -> new DestinationNotFoundException(request.destination()));

        return getRecommendationsForLocation(geocodedDestination.location(), request.arrivalTime());
    }

    private ParkingRecommendationResponse getRecommendationsForLocation(
            Location destination,
            LocalDateTime arrivalTime
    ) {
        List<ParkingRecommendation> recommendations = parkingDataProvider.findNearbySpots(destination).stream()
                .map(spot -> buildRecommendation(spot, destination, arrivalTime))
                .sorted(Comparator.comparingDouble(ParkingRecommendation::score).reversed())
                .toList();

        String bestOptionSummary = buildBestOptionSummary(recommendations);
        saveSearchHistory(destination, arrivalTime, bestOptionSummary);

        return new ParkingRecommendationResponse(bestOptionSummary, recommendations);
    }

    private void saveSearchHistory(Location destination, LocalDateTime arrivalTime, String bestOptionSummary) {
        SearchHistory searchHistory = new SearchHistory(
                destination.latitude(),
                destination.longitude(),
                arrivalTime,
                LocalDateTime.now(),
                bestOptionSummary
        );

        searchHistoryRepository.save(searchHistory);
    }

    private ParkingRecommendation buildRecommendation(
            ParkingSpot parkingSpot,
            Location destination,
            LocalDateTime arrivalTime
    ) {
        double distanceMeters = DistanceCalculator.calculateDistanceMeters(destination, parkingSpot.location());
        double predictedAvailability = availabilityPredictionService.predictAvailability(arrivalTime);
        double predictedPrice = pricePredictionService.predictPrice(parkingSpot, arrivalTime);
        double score = recommendationScoringService.calculateScore(
                predictedAvailability,
                predictedPrice,
                distanceMeters
        );
        String explanation = recommendationExplanationService.generateExplanation(
                predictedAvailability,
                predictedPrice,
                distanceMeters
        );

        return new ParkingRecommendation(
                parkingSpot.id(),
                parkingSpot.name(),
                parkingSpot.address(),
                parkingSpot.category(),
                parkingSpot.location().latitude(),
                parkingSpot.location().longitude(),
                parkingDataProvider.getProviderType(),
                roundToOneDecimal(distanceMeters),
                predictedAvailability,
                predictedPrice,
                score,
                explanation
        );
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String buildBestOptionSummary(List<ParkingRecommendation> recommendations) {
        if (recommendations.isEmpty()) {
            return "No parking recommendations available for the selected destination and arrival time";
        }

        ParkingRecommendation bestRecommendation = recommendations.get(0);
        return bestRecommendation.spotName() + " is the top recommendation based on availability, price, and distance";
    }
}
