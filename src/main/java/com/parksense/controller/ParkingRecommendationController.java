package com.parksense.controller;

import com.parksense.model.ParkingRecommendationRequest;
import com.parksense.model.ParkingRecommendationResponse;
import com.parksense.service.ParkingRecommendationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommendations")
public class ParkingRecommendationController {

    private final ParkingRecommendationService parkingRecommendationService;

    public ParkingRecommendationController(ParkingRecommendationService parkingRecommendationService) {
        this.parkingRecommendationService = parkingRecommendationService;
    }

    @PostMapping
    public ParkingRecommendationResponse getRecommendations(@RequestBody ParkingRecommendationRequest request) {
        return parkingRecommendationService.getRecommendations(request);
    }
}
