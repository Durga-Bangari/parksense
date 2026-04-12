package com.parksense.config;

import com.parksense.provider.GoogleMapsParkingDataProvider;
import com.parksense.provider.MockParkingDataProvider;
import com.parksense.provider.ParkingDataProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParkingProviderConfigTest {

    private final ParkingProviderConfig parkingProviderConfig = new ParkingProviderConfig();

    @Test
    void returnsMockProviderWhenConfiguredTypeIsMock() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setType("mock");

        ParkingDataProvider provider =
                parkingProviderConfig.parkingDataProvider(
                        properties,
                        new MockParkingDataProvider(),
                        new GoogleMapsParkingDataProvider(null)
                );

        assertInstanceOf(MockParkingDataProvider.class, provider);
    }

    @Test
    void returnsGoogleMapsProviderWhenConfiguredTypeIsGoogleMaps() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setType("google-maps");

        ParkingDataProvider provider =
                parkingProviderConfig.parkingDataProvider(
                        properties,
                        new MockParkingDataProvider(),
                        new GoogleMapsParkingDataProvider(null)
                );

        assertInstanceOf(GoogleMapsParkingDataProvider.class, provider);
    }

    @Test
    void throwsForUnsupportedProviderType() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setType("unknown");

        assertThrows(
                IllegalArgumentException.class,
                () -> parkingProviderConfig.parkingDataProvider(
                        properties,
                        new MockParkingDataProvider(),
                        new GoogleMapsParkingDataProvider(null)
                )
        );
    }
}
