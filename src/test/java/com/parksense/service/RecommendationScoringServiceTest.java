package com.parksense.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecommendationScoringServiceTest {

    private final RecommendationScoringService recommendationScoringService =
            new RecommendationScoringService();

    @Test
    void givesHigherScoresToStrongerRecommendations() {
        double strongScore = recommendationScoringService.calculateScore(0.85, 10.00, 250.0);
        double weakScore = recommendationScoringService.calculateScore(0.35, 20.00, 1500.0);

        assertTrue(strongScore > weakScore);
    }

    @Test
    void roundsScoreToOneDecimalPlace() {
        double score = recommendationScoringService.calculateScore(0.75, 12.50, 200.0);

        assertEquals(7.1, score, 0.001);
    }

    @Test
    void clampsPriceAndDistanceNormalizationAtLowerBound() {
        double score = recommendationScoringService.calculateScore(0.50, 40.00, 5000.0);

        assertEquals(2.5, score, 0.001);
    }
}
