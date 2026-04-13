package com.parksense.config;

import com.parksense.geocoding.DestinationGeocodingProvider;
import com.parksense.geocoding.GoogleMapsDestinationGeocodingProvider;
import com.parksense.geocoding.MockDestinationGeocodingProvider;
import com.parksense.integration.googlemaps.client.GoogleMapsGeocodingClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class GeocodingProviderConfigTest {

    private final GeocodingProviderConfig geocodingProviderConfig = new GeocodingProviderConfig();

    @Test
    void returnsMockProviderWhenConfiguredTypeIsMock() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setGeocodingType("mock");

        DestinationGeocodingProvider provider =
                geocodingProviderConfig.destinationGeocodingProvider(
                        properties,
                        new MockDestinationGeocodingProvider(),
                        new GoogleMapsDestinationGeocodingProvider(mock(GoogleMapsGeocodingClient.class))
                );

        assertInstanceOf(MockDestinationGeocodingProvider.class, provider);
    }

    @Test
    void returnsGoogleMapsProviderWhenConfiguredTypeIsGoogleMaps() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setGeocodingType("google-maps");

        DestinationGeocodingProvider provider =
                geocodingProviderConfig.destinationGeocodingProvider(
                        properties,
                        new MockDestinationGeocodingProvider(),
                        new GoogleMapsDestinationGeocodingProvider(mock(GoogleMapsGeocodingClient.class))
                );

        assertInstanceOf(GoogleMapsDestinationGeocodingProvider.class, provider);
    }

    @Test
    void throwsForUnsupportedProviderType() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setGeocodingType("unknown");

        assertThrows(
                IllegalArgumentException.class,
                () -> geocodingProviderConfig.destinationGeocodingProvider(
                        properties,
                        new MockDestinationGeocodingProvider(),
                        new GoogleMapsDestinationGeocodingProvider(mock(GoogleMapsGeocodingClient.class))
                )
        );
    }
}
