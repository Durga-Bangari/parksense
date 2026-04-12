package com.parksense.integration.googlemaps.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleMapsDisplayName(
        String text,
        String languageCode
) {
}
