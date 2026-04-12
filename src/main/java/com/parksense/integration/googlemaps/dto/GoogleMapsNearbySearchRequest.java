package com.parksense.integration.googlemaps.dto;

import java.util.List;

public record GoogleMapsNearbySearchRequest(
        List<String> includedTypes,
        int maxResultCount,
        GoogleMapsLocationRestriction locationRestriction,
        String rankPreference
) {
}
