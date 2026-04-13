package com.parksense.model;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ParkingRecommendationRequest(
        @NotNull(message = "latitude is required")
        @DecimalMin(value = "-90.0", message = "latitude must be greater than or equal to -90")
        @DecimalMax(value = "90.0", message = "latitude must be less than or equal to 90")
        Double latitude,
        @NotNull(message = "longitude is required")
        @DecimalMin(value = "-180.0", message = "longitude must be greater than or equal to -180")
        @DecimalMax(value = "180.0", message = "longitude must be less than or equal to 180")
        Double longitude,
        @NotNull(message = "arrivalTime is required")
        LocalDateTime arrivalTime
) {
}
