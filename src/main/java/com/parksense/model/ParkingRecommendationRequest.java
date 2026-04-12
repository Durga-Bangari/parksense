package com.parksense.model;

import java.time.LocalDateTime;

public record ParkingRecommendationRequest(
        double latitude,
        double longitude,
        LocalDateTime arrivalTime
) {
}
