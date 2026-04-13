package com.parksense.geocoding;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ExternalProviderException;
import com.parksense.exception.ProviderConfigurationException;
import com.parksense.integration.googlemaps.client.GoogleMapsGeocodingClient;
import com.parksense.model.GeocodedDestination;
import com.parksense.model.Location;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GoogleMapsDestinationGeocodingProviderTest {

    @Test
    void delegatesGeocodingToClient() {
        GoogleMapsGeocodingClient client = mock(GoogleMapsGeocodingClient.class);
        MockDestinationGeocodingProvider mockProvider = new MockDestinationGeocodingProvider();
        ParkingProviderProperties properties = new ParkingProviderProperties();
        given(client.geocode("Space Needle"))
                .willReturn(Optional.of(new GeocodedDestination("Space Needle", new Location(47.6205, -122.3493))));

        GoogleMapsDestinationGeocodingProvider provider =
                new GoogleMapsDestinationGeocodingProvider(client, mockProvider, properties);

        Optional<GeocodedDestination> result = provider.geocode("Space Needle");

        assertTrue(result.isPresent());
        assertEquals("Space Needle", result.get().displayName());
    }

    @Test
    void fallsBackToMockProviderWhenExternalLookupFails() {
        GoogleMapsGeocodingClient client = mock(GoogleMapsGeocodingClient.class);
        MockDestinationGeocodingProvider mockProvider = new MockDestinationGeocodingProvider();
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setFallbackToMockOnFailure(true);
        given(client.geocode("Space Needle"))
                .willThrow(new ExternalProviderException("google-maps", "Google Maps geocoding request failed"));

        GoogleMapsDestinationGeocodingProvider provider =
                new GoogleMapsDestinationGeocodingProvider(client, mockProvider, properties);

        Optional<GeocodedDestination> result = provider.geocode("Space Needle");

        assertTrue(result.isPresent());
        assertEquals("Space Needle", result.get().displayName());
    }

    @Test
    void fallsBackToMockProviderWhenGoogleReturnsNoResults() {
        GoogleMapsGeocodingClient client = mock(GoogleMapsGeocodingClient.class);
        MockDestinationGeocodingProvider mockProvider = new MockDestinationGeocodingProvider();
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setFallbackToMockOnFailure(true);
        given(client.geocode("Seattle Convention Center")).willReturn(Optional.empty());

        GoogleMapsDestinationGeocodingProvider provider =
                new GoogleMapsDestinationGeocodingProvider(client, mockProvider, properties);

        Optional<GeocodedDestination> result = provider.geocode("Seattle Convention Center");

        assertTrue(result.isPresent());
        assertEquals("Seattle Convention Center", result.get().displayName());
    }

    @Test
    void rethrowsExternalFailureWhenFallbackIsDisabled() {
        GoogleMapsGeocodingClient client = mock(GoogleMapsGeocodingClient.class);
        MockDestinationGeocodingProvider mockProvider = new MockDestinationGeocodingProvider();
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setFallbackToMockOnFailure(false);
        given(client.geocode("Space Needle"))
                .willThrow(new ExternalProviderException("google-maps", "Google Maps geocoding request failed"));

        GoogleMapsDestinationGeocodingProvider provider =
                new GoogleMapsDestinationGeocodingProvider(client, mockProvider, properties);

        assertThrows(
                ExternalProviderException.class,
                () -> provider.geocode("Space Needle")
        );
    }

    @Test
    void rethrowsConfigurationFailureInsteadOfFallingBack() {
        GoogleMapsGeocodingClient client = mock(GoogleMapsGeocodingClient.class);
        MockDestinationGeocodingProvider mockProvider = new MockDestinationGeocodingProvider();
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setFallbackToMockOnFailure(true);
        given(client.geocode("Space Needle"))
                .willThrow(new ProviderConfigurationException(
                        "google-maps",
                        "Google Maps API key is required when geocoding provider type is google-maps"
                ));

        GoogleMapsDestinationGeocodingProvider provider =
                new GoogleMapsDestinationGeocodingProvider(client, mockProvider, properties);

        assertThrows(
                ProviderConfigurationException.class,
                () -> provider.geocode("Space Needle")
        );
    }
}
