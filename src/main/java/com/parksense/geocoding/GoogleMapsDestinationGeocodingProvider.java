package com.parksense.geocoding;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ExternalProviderException;
import com.parksense.exception.ProviderConfigurationException;
import com.parksense.integration.googlemaps.client.GoogleMapsGeocodingClient;
import com.parksense.model.GeocodedDestination;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GoogleMapsDestinationGeocodingProvider implements DestinationGeocodingProvider {

    private final GoogleMapsGeocodingClient googleMapsGeocodingClient;
    private final MockDestinationGeocodingProvider mockDestinationGeocodingProvider;
    private final ParkingProviderProperties properties;

    public GoogleMapsDestinationGeocodingProvider(
            GoogleMapsGeocodingClient googleMapsGeocodingClient,
            MockDestinationGeocodingProvider mockDestinationGeocodingProvider,
            ParkingProviderProperties properties
    ) {
        this.googleMapsGeocodingClient = googleMapsGeocodingClient;
        this.mockDestinationGeocodingProvider = mockDestinationGeocodingProvider;
        this.properties = properties;
    }

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public Optional<GeocodedDestination> geocode(String destinationQuery) {
        try {
            Optional<GeocodedDestination> geocodedDestination = googleMapsGeocodingClient.geocode(destinationQuery);
            if (geocodedDestination.isPresent()) {
                return geocodedDestination;
            }
        } catch (ProviderConfigurationException exception) {
            throw exception;
        } catch (ExternalProviderException exception) {
            if (!properties.isFallbackToMockOnFailure()) {
                throw exception;
            }

            return mockDestinationGeocodingProvider.geocode(destinationQuery);
        }

        if (properties.isFallbackToMockOnFailure()) {
            return mockDestinationGeocodingProvider.geocode(destinationQuery);
        }

        return Optional.empty();
    }
}
