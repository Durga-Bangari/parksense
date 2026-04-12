package com.parksense.integration.googlemaps.client;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ProviderConfigurationException;
import com.parksense.integration.googlemaps.dto.GoogleMapsNearbySearchRequest;
import com.parksense.model.Location;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoogleMapsPlacesClientTest {

    @Test
    void createsNearbySearchRequestUsingConfiguredRadius() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setSearchRadiusMeters(1800);
        GoogleMapsPlacesClient client = new GoogleMapsPlacesClient(RestClient.builder(), properties);

        GoogleMapsNearbySearchRequest request =
                client.createNearbySearchRequest(new Location(47.6097, -122.3331));

        assertEquals("parking", request.includedTypes().get(0));
        assertEquals(1800, request.locationRestriction().circle().radius(), 0.001);
        assertEquals("DISTANCE", request.rankPreference());
    }

    @Test
    void throwsWhenGoogleMapsApiKeyIsMissing() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        GoogleMapsPlacesClient client = new GoogleMapsPlacesClient(RestClient.builder(), properties);

        assertThrows(
                ProviderConfigurationException.class,
                () -> client.searchNearbyParking(new Location(47.6097, -122.3331))
        );
    }
}
