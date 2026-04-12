package com.parksense.config;

import com.parksense.provider.GoogleMapsParkingDataProvider;
import com.parksense.provider.MockParkingDataProvider;
import com.parksense.provider.ParkingDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParkingProviderConfig {

    @Bean
    public ParkingDataProvider parkingDataProvider(
            ParkingProviderProperties properties,
            MockParkingDataProvider mockParkingDataProvider,
            GoogleMapsParkingDataProvider googleMapsParkingDataProvider
    ) {
        return switch (properties.getType()) {
            case "google-maps" -> googleMapsParkingDataProvider;
            case "mock" -> mockParkingDataProvider;
            default -> throw new IllegalArgumentException(
                    "Unsupported parking provider type: " + properties.getType()
            );
        };
    }
}
