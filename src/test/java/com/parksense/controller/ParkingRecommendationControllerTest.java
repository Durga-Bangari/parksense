package com.parksense.controller;

import com.parksense.model.ParkingRecommendation;
import com.parksense.model.ParkingRecommendationResponse;
import com.parksense.service.ParkingRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParkingRecommendationController.class)
class ParkingRecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkingRecommendationService parkingRecommendationService;

    @Test
    void returnsRankedParkingRecommendations() throws Exception {
        ParkingRecommendation recommendation = new ParkingRecommendation(
                "P1",
                "Central Garage",
                "1200 4th Ave, Seattle, WA",
                "garage",
                47.6097,
                -122.3331,
                "mock",
                200.0,
                0.75,
                12.50,
                8.7,
                "High availability and low price"
        );

        given(parkingRecommendationService.getRecommendations(any()))
                .willReturn(new ParkingRecommendationResponse(
                        "Central Garage is the top recommendation based on availability, price, and distance",
                        List.of(recommendation)
                ));

        mockMvc.perform(post("/api/v1/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "latitude": 47.6,
                                  "longitude": -122.3,
                                  "arrivalTime": "2026-04-12T18:00:00"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bestOptionSummary")
                        .value("Central Garage is the top recommendation based on availability, price, and distance"))
                .andExpect(jsonPath("$.recommendations[0].spotId").value("P1"))
                .andExpect(jsonPath("$.recommendations[0].spotName").value("Central Garage"))
                .andExpect(jsonPath("$.recommendations[0].address").value("1200 4th Ave, Seattle, WA"))
                .andExpect(jsonPath("$.recommendations[0].category").value("garage"))
                .andExpect(jsonPath("$.recommendations[0].latitude").value(47.6097))
                .andExpect(jsonPath("$.recommendations[0].longitude").value(-122.3331))
                .andExpect(jsonPath("$.recommendations[0].providerType").value("mock"))
                .andExpect(jsonPath("$.recommendations[0].score").value(8.7));
    }

    @Test
    void returnsBadRequestForInvalidCoordinates() throws Exception {
        mockMvc.perform(post("/api/v1/recommendations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "latitude": 120.0,
                                  "longitude": -122.3,
                                  "arrivalTime": "2026-04-12T18:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.details[0]").value("latitude must be less than or equal to 90"));
    }
}
