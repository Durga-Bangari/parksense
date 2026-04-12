package com.parksense.model;

public record ParkingSpot(
        String id,
        String name,
        Location location,
        double basePricePerHour
) {
}
