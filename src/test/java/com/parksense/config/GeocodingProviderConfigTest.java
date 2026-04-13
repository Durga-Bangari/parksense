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
    private final ParkingProviderProperties properties = new ParkingProviderProperties();
    private final MockDestinationGeocodingProvider mockDestinationGeocodingProvider =
            new MockDestinationGeocodingProvider();

    @Test
    void returnsMockProviderWhenConfiguredTypeIsMock() {
        properties.setGeocodingType("mock");

        DestinationGeocodingProvider provider =
                geocodingProviderConfig.destinationGeocodingProvider(
                        properties,
                        mockDestinationGeocodingProvider,
                        new GoogleMapsDestinationGeocodingProvider(
                                mock(GoogleMapsGeocodingClient.class),
                                mockDestinationGeocodingProvider,
                                properties
                        )
                );

        assertInstanceOf(MockDestinationGeocodingProvider.class, provider);
    }

    @Test
    void returnsGoogleMapsProviderWhenConfiguredTypeIsGoogleMaps() {
        properties.setGeocodingType("google-maps");

        DestinationGeocodingProvider provider =
                geocodingProviderConfig.destinationGeocodingProvider(
                        properties,
                        mockDestinationGeocodingProvider,
                        new GoogleMapsDestinationGeocodingProvider(
                                mock(GoogleMapsGeocodingClient.class),
                                mockDestinationGeocodingProvider,
                                properties
                        )
                );

        assertInstanceOf(GoogleMapsDestinationGeocodingProvider.class, provider);
    }

    @Test
    void throwsForUnsupportedProviderType() {
        properties.setGeocodingType("unknown");

        assertThrows(
                IllegalArgumentException.class,
                () -> geocodingProviderConfig.destinationGeocodingProvider(
                        properties,
                        mockDestinationGeocodingProvider,
                        new GoogleMapsDestinationGeocodingProvider(
                                mock(GoogleMapsGeocodingClient.class),
                                mockDestinationGeocodingProvider,
                                properties
                        )
                )
        );
    }
}
