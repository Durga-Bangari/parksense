package com.parksense.model;

import java.util.List;

public record ParkingRecommendationResponse(
        List<ParkingRecommendation> recommendations
) {
}
