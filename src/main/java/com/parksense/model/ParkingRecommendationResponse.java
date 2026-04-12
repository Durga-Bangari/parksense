package com.parksense.model;

import java.util.List;

public record ParkingRecommendationResponse(
        String bestOptionSummary,
        List<ParkingRecommendation> recommendations
) {
}
