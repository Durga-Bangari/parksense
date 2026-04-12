package com.parksense.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AvailabilityPredictionServiceTest {

    private final AvailabilityPredictionService availabilityPredictionService =
            new AvailabilityPredictionService();

    @Test
    void lowersAvailabilityDuringWeekdayMorningCommute() {
        LocalDateTime arrivalTime = LocalDateTime.of(2026, 4, 13, 8, 0);

        double availability = availabilityPredictionService.predictAvailability(arrivalTime);

        assertEquals(0.40, availability, 0.001);
    }

    @Test
    void increasesAvailabilityForWeekendEvening() {
        LocalDateTime arrivalTime = LocalDateTime.of(2026, 4, 12, 21, 0);

        double availability = availabilityPredictionService.predictAvailability(arrivalTime);

        assertEquals(0.95, availability, 0.001);
    }

    @Test
    void alwaysReturnsAValueBetweenZeroAndOne() {
        LocalDateTime arrivalTime = LocalDateTime.of(2026, 4, 14, 12, 0);

        double availability = availabilityPredictionService.predictAvailability(arrivalTime);

        assertTrue(availability >= 0.0);
        assertTrue(availability <= 1.0);
    }
}
