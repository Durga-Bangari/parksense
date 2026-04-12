package com.parksense.integration.googlemaps.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleMapsPlace(
        String id,
        GoogleMapsDisplayName displayName,
        GoogleMapsLatLng location,
        String primaryType
) {
}
