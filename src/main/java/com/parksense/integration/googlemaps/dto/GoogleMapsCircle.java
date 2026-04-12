package com.parksense.integration.googlemaps.dto;

public record GoogleMapsCircle(
        GoogleMapsLatLng center,
        double radius
) {
}
