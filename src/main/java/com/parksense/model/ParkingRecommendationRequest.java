package com.parksense.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ParkingRecommendationRequest(
        @DecimalMin(value = "-90.0", message = "latitude must be greater than or equal to -90")
        @DecimalMax(value = "90.0", message = "latitude must be less than or equal to 90")
        double latitude,
        @DecimalMin(value = "-180.0", message = "longitude must be greater than or equal to -180")
        @DecimalMax(value = "180.0", message = "longitude must be less than or equal to 180")
        double longitude,
        @NotNull(message = "arrivalTime is required")
        LocalDateTime arrivalTime
) {
}
