package com.parksense.config;

import com.parksense.geocoding.DestinationGeocodingProvider;
import com.parksense.geocoding.GoogleMapsDestinationGeocodingProvider;
import com.parksense.geocoding.MockDestinationGeocodingProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeocodingProviderConfig {

    @Bean
    public DestinationGeocodingProvider destinationGeocodingProvider(
            ParkingProviderProperties properties,
            MockDestinationGeocodingProvider mockDestinationGeocodingProvider,
            GoogleMapsDestinationGeocodingProvider googleMapsDestinationGeocodingProvider
    ) {
        return switch (properties.getGeocodingType()) {
            case "google-maps" -> googleMapsDestinationGeocodingProvider;
            case "mock" -> mockDestinationGeocodingProvider;
            default -> throw new IllegalArgumentException(
                    "Unsupported geocoding provider type: " + properties.getGeocodingType()
            );
        };
    }
}
