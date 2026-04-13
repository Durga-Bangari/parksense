package com.parksense.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DestinationRecommendationRequest(
        @NotBlank(message = "destination is required")
        String destination,
        @NotNull(message = "arrivalTime is required")
        LocalDateTime arrivalTime
) {
}
