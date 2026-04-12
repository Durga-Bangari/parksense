package com.parksense.model;

public record ParkingRecommendation(
        String spotId,
        String spotName,
        double distanceMeters,
        double predictedAvailability,
        double predictedPrice,
        double score,
        String explanation
) {
}
