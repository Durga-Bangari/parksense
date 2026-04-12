package com.parksense.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
public class AvailabilityPredictionService {

    public double predictAvailability(LocalDateTime arrivalTime) {
        double availabilityScore = 0.65;

        if (isWeekend(arrivalTime)) {
            availabilityScore += 0.15;
        } else if (isMorningCommute(arrivalTime) || isEveningCommute(arrivalTime)) {
            availabilityScore -= 0.25;
        } else if (isMidday(arrivalTime)) {
            availabilityScore -= 0.10;
        }

        if (isLateNight(arrivalTime)) {
            availabilityScore += 0.15;
        }

        return clamp(availabilityScore);
    }

    private boolean isWeekend(LocalDateTime arrivalTime) {
        DayOfWeek dayOfWeek = arrivalTime.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    private boolean isMorningCommute(LocalDateTime arrivalTime) {
        int hour = arrivalTime.getHour();
        return hour >= 7 && hour < 10;
    }

    private boolean isMidday(LocalDateTime arrivalTime) {
        int hour = arrivalTime.getHour();
        return hour >= 11 && hour < 16;
    }

    private boolean isEveningCommute(LocalDateTime arrivalTime) {
        int hour = arrivalTime.getHour();
        return hour >= 17 && hour < 20;
    }

    private boolean isLateNight(LocalDateTime arrivalTime) {
        int hour = arrivalTime.getHour();
        return hour >= 21 || hour < 6;
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }
}
