package com.parksense.service;

import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PricePredictionServiceTest {

    private final PricePredictionService pricePredictionService = new PricePredictionService();

    private final ParkingSpot parkingSpot = new ParkingSpot(
            "P1",
            "Central Garage",
            new Location(47.6097, -122.3331),
            "1200 4th Ave, Seattle, WA",
            "garage",
            12.00
    );

    @Test
    void increasesPriceDuringWeekdayPeakDemand() {
        LocalDateTime arrivalTime = LocalDateTime.of(2026, 4, 13, 8, 0);

        double predictedPrice = pricePredictionService.predictPrice(parkingSpot, arrivalTime);

        assertEquals(15.00, predictedPrice, 0.001);
    }

    @Test
    void lowersPriceForWeekendLateNightParking() {
        LocalDateTime arrivalTime = LocalDateTime.of(2026, 4, 12, 22, 0);

        double predictedPrice = pricePredictionService.predictPrice(parkingSpot, arrivalTime);

        assertEquals(9.00, predictedPrice, 0.001);
    }

    @Test
    void appliesModerateIncreaseDuringMidday() {
        LocalDateTime arrivalTime = LocalDateTime.of(2026, 4, 14, 13, 0);

        double predictedPrice = pricePredictionService.predictPrice(parkingSpot, arrivalTime);

        assertEquals(13.20, predictedPrice, 0.001);
    }
}
