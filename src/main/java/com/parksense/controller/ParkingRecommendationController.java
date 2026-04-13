package com.parksense.controller;

import com.parksense.model.DestinationRecommendationRequest;
import com.parksense.model.ParkingRecommendationRequest;
import com.parksense.model.ParkingRecommendationResponse;
import com.parksense.service.ParkingRecommendationService;
import jakarta.validation.Valid;
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
    public ParkingRecommendationResponse getRecommendations(@Valid @RequestBody ParkingRecommendationRequest request) {
        return parkingRecommendationService.getRecommendations(request);
    }

    @PostMapping("/by-destination")
    public ParkingRecommendationResponse getRecommendationsByDestination(
            @Valid @RequestBody DestinationRecommendationRequest request
    ) {
        return parkingRecommendationService.getRecommendationsByDestination(request);
    }
}
