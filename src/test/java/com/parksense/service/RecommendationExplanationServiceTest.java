package com.parksense.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecommendationExplanationServiceTest {

    private final RecommendationExplanationService recommendationExplanationService =
            new RecommendationExplanationService();

    @Test
    void highlightsStrongRecommendationSignals() {
        String explanation = recommendationExplanationService.generateExplanation(0.82, 10.50, 220.0);

        assertEquals("High availability, low price, and short walking distance", explanation);
    }

    @Test
    void returnsBalancedExplanationWhenNoStrongSignalExists() {
        String explanation = recommendationExplanationService.generateExplanation(0.60, 14.50, 550.0);

        assertEquals("Balanced option with moderate availability, price, and distance", explanation);
    }

    @Test
    void canDescribeTradeoffsForWeakerOptions() {
        String explanation = recommendationExplanationService.generateExplanation(0.35, 19.00, 1400.0);

        assertEquals("Limited availability, higher price, and longer walking distance", explanation);
    }
}
