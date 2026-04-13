package com.parksense.integration.googlemaps.dto;

import java.util.List;

public record GoogleMapsGeocodingResponse(
        List<GoogleMapsGeocodingResult> results
) {
}
