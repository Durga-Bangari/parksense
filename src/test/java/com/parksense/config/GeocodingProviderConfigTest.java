package com.parksense.config;

import com.parksense.geocoding.DestinationGeocodingProvider;
import com.parksense.geocoding.GoogleMapsDestinationGeocodingProvider;
import com.parksense.geocoding.MockDestinationGeocodingProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                        new GoogleMapsDestinationGeocodingProvider()
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
                        new GoogleMapsDestinationGeocodingProvider()
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
                        new GoogleMapsDestinationGeocodingProvider()
                )
        );
    }
}
