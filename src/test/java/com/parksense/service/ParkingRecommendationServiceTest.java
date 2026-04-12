package com.parksense.service;

import com.parksense.model.ParkingRecommendation;
import com.parksense.model.ParkingRecommendationRequest;
import com.parksense.model.ParkingRecommendationResponse;
import com.parksense.repository.ParkingSpotRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParkingRecommendationServiceTest {

    private final ParkingRecommendationService parkingRecommendationService =
            new ParkingRecommendationService(
                    new ParkingSpotRepository(),
                    new AvailabilityPredictionService(),
                    new PricePredictionService(),
                    new RecommendationScoringService(),
                    new RecommendationExplanationService()
            );

    @Test
    void returnsRankedRecommendationsForArrivalRequest() {
        ParkingRecommendationRequest request = new ParkingRecommendationRequest(
                47.6090,
                -122.3340,
                LocalDateTime.of(2026, 4, 14, 18, 0)
        );

        ParkingRecommendationResponse response = parkingRecommendationService.getRecommendations(request);

        List<ParkingRecommendation> recommendations = response.recommendations();

        assertEquals(5, recommendations.size());
        assertFalse(recommendations.isEmpty());
        assertTrue(recommendations.get(0).score() >= recommendations.get(1).score());
    }

    @Test
    void includesExplanationAndComputedFieldsInEachRecommendation() {
        ParkingRecommendationRequest request = new ParkingRecommendationRequest(
                47.6090,
                -122.3340,
                LocalDateTime.of(2026, 4, 12, 21, 0)
        );

        ParkingRecommendation firstRecommendation =
                parkingRecommendationService.getRecommendations(request).recommendations().getFirst();

        assertTrue(firstRecommendation.distanceMeters() > 0.0);
        assertTrue(firstRecommendation.predictedAvailability() >= 0.0);
        assertTrue(firstRecommendation.predictedPrice() > 0.0);
        assertFalse(firstRecommendation.explanation().isBlank());
    }
}
