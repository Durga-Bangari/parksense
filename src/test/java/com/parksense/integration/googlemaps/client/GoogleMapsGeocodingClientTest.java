package com.parksense.integration.googlemaps.client;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ProviderConfigurationException;
import com.parksense.integration.googlemaps.dto.GoogleMapsGeocodingResponse;
import com.parksense.integration.googlemaps.dto.GoogleMapsGeocodingResult;
import com.parksense.integration.googlemaps.dto.GoogleMapsGeometry;
import com.parksense.integration.googlemaps.dto.GoogleMapsLatLng;
import com.parksense.model.GeocodedDestination;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GoogleMapsGeocodingClientTest {

    @Test
    void createsGeocodingPathWithDestinationAndApiKey() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setGoogleMapsApiKey("demo-key");
        GoogleMapsGeocodingClient client = new GoogleMapsGeocodingClient(RestClient.builder(), properties);

        String path = client.createGeocodingPath("Space Needle");

        assertTrue(path.startsWith("/json?"));
        assertTrue(path.contains("address=Space"));
        assertTrue(path.contains("Needle"));
        assertTrue(path.contains("key=demo-key"));
    }

    @Test
    void throwsWhenGoogleMapsApiKeyIsMissing() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        GoogleMapsGeocodingClient client = new GoogleMapsGeocodingClient(RestClient.builder(), properties);

        assertThrows(
                ProviderConfigurationException.class,
                () -> client.geocode("Space Needle")
        );
    }

    @Test
    void mapsFirstUsableGeocodingResult() {
        ParkingProviderProperties properties = new ParkingProviderProperties();
        properties.setGoogleMapsApiKey("demo-key");
        GoogleMapsGeocodingClient client = new GoogleMapsGeocodingClient(RestClient.builder(), properties);

        GoogleMapsGeocodingResponse response = new GoogleMapsGeocodingResponse(
                List.of(
                        new GoogleMapsGeocodingResult(
                                "Seattle Convention Center, Seattle, WA, USA",
                                new GoogleMapsGeometry(new GoogleMapsLatLng(47.6114, -122.3317))
                        )
                )
        );

        Optional<GeocodedDestination> result = client.mapResponse(response);

        assertTrue(result.isPresent());
        assertEquals("Seattle Convention Center, Seattle, WA, USA", result.get().displayName());
        assertEquals(47.6114, result.get().location().latitude());
        assertEquals(-122.3317, result.get().location().longitude());
    }
}
