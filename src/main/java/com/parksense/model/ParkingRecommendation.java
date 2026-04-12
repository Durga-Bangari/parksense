package com.parksense.model;

public record ParkingRecommendation(
        String spotId,
        String spotName,
        String address,
        String category,
        double latitude,
        double longitude,
        String providerType,
        double distanceMeters,
        double predictedAvailability,
        double predictedPrice,
        double score,
        String explanation
) {
}
