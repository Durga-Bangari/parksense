package com.parksense.geocoding;

import com.parksense.model.GeocodedDestination;

import java.util.Optional;

public interface DestinationGeocodingProvider {

    String getProviderType();

    Optional<GeocodedDestination> geocode(String destinationQuery);
}
