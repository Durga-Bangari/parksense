package com.parksense.geocoding;

import com.parksense.model.GeocodedDestination;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleMapsDestinationGeocodingProvider implements DestinationGeocodingProvider {

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public Optional<GeocodedDestination> geocode(String destinationQuery) {
        return Optional.empty();
    }
}
