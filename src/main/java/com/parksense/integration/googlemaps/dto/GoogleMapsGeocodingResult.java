package com.parksense.integration.googlemaps.dto;

public record GoogleMapsGeocodingResult(
        String formatted_address,
        GoogleMapsGeometry geometry
) {
}
