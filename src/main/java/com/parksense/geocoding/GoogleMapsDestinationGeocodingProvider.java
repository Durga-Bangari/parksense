package com.parksense.geocoding;

import com.parksense.integration.googlemaps.client.GoogleMapsGeocodingClient;
import com.parksense.model.GeocodedDestination;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleMapsDestinationGeocodingProvider implements DestinationGeocodingProvider {

    private final GoogleMapsGeocodingClient googleMapsGeocodingClient;

    public GoogleMapsDestinationGeocodingProvider(GoogleMapsGeocodingClient googleMapsGeocodingClient) {
        this.googleMapsGeocodingClient = googleMapsGeocodingClient;
    }

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public Optional<GeocodedDestination> geocode(String destinationQuery) {
        return googleMapsGeocodingClient.geocode(destinationQuery);
    }
}
