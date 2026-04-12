package com.parksense.service;

import com.parksense.model.ParkingSpot;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
public class PricePredictionService {

    public double predictPrice(ParkingSpot parkingSpot, LocalDateTime arrivalTime) {
        double multiplier = 1.0;

        if (isWeekend(arrivalTime)) {
            multiplier -= 0.10;
        } else if (isMorningCommute(arrivalTime) || isEveningCommute(arrivalTime)) {
            multiplier += 0.25;
        } else if (isMidday(arrivalTime)) {
            multiplier += 0.10;
        }

        if (isLateNight(arrivalTime)) {
            multiplier -= 0.15;
        }

        return roundToTwoDecimals(parkingSpot.basePricePerHour() * multiplier);
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

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
