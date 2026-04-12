package com.parksense.service;

import com.parksense.model.Location;
import com.parksense.model.ParkingRecommendation;
import com.parksense.model.ParkingRecommendationRequest;
import com.parksense.model.ParkingRecommendationResponse;
import com.parksense.model.ParkingSpot;
import com.parksense.repository.ParkingSpotRepository;
import com.parksense.util.DistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ParkingRecommendationService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final AvailabilityPredictionService availabilityPredictionService;
    private final PricePredictionService pricePredictionService;
    private final RecommendationScoringService recommendationScoringService;
    private final RecommendationExplanationService recommendationExplanationService;

    public ParkingRecommendationService(
            ParkingSpotRepository parkingSpotRepository,
            AvailabilityPredictionService availabilityPredictionService,
            PricePredictionService pricePredictionService,
            RecommendationScoringService recommendationScoringService,
            RecommendationExplanationService recommendationExplanationService
    ) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.availabilityPredictionService = availabilityPredictionService;
        this.pricePredictionService = pricePredictionService;
        this.recommendationScoringService = recommendationScoringService;
        this.recommendationExplanationService = recommendationExplanationService;
    }

    public ParkingRecommendationResponse getRecommendations(ParkingRecommendationRequest request) {
        Location destination = new Location(request.latitude(), request.longitude());

        List<ParkingRecommendation> recommendations = parkingSpotRepository.findAll().stream()
                .map(spot -> buildRecommendation(spot, destination, request))
                .sorted(Comparator.comparingDouble(ParkingRecommendation::score).reversed())
                .toList();

        return new ParkingRecommendationResponse(recommendations);
    }

    private ParkingRecommendation buildRecommendation(
            ParkingSpot parkingSpot,
            Location destination,
            ParkingRecommendationRequest request
    ) {
        double distanceMeters = DistanceCalculator.calculateDistanceMeters(destination, parkingSpot.location());
        double predictedAvailability = availabilityPredictionService.predictAvailability(request.arrivalTime());
        double predictedPrice = pricePredictionService.predictPrice(parkingSpot, request.arrivalTime());
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
}
