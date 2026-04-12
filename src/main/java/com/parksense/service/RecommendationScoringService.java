package com.parksense.service;

import org.springframework.stereotype.Service;

@Service
public class RecommendationScoringService {

    private static final double MAX_DISTANCE_METERS = 2_000.0;
    private static final double MAX_PRICE = 25.0;

    public double calculateScore(double availability, double predictedPrice, double distanceMeters) {
        double availabilityComponent = availability * 10.0 * 0.5;
        double priceComponent = normalizePrice(predictedPrice) * 10.0 * 0.3;
        double distanceComponent = normalizeDistance(distanceMeters) * 10.0 * 0.2;

        return roundToOneDecimal(availabilityComponent + priceComponent + distanceComponent);
    }

    private double normalizePrice(double predictedPrice) {
        double normalized = 1.0 - (predictedPrice / MAX_PRICE);
        return clamp(normalized);
    }

    private double normalizeDistance(double distanceMeters) {
        double normalized = 1.0 - (distanceMeters / MAX_DISTANCE_METERS);
        return clamp(normalized);
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
