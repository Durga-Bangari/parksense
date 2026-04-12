package com.parksense.integration.googlemaps.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleMapsNearbySearchResponse(
        List<GoogleMapsPlace> places
) {
}
