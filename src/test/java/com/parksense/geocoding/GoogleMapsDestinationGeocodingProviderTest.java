package com.parksense.geocoding;

import com.parksense.integration.googlemaps.client.GoogleMapsGeocodingClient;
import com.parksense.model.GeocodedDestination;
import com.parksense.model.Location;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class GoogleMapsDestinationGeocodingProviderTest {

    @Test
    void delegatesGeocodingToClient() {
        GoogleMapsGeocodingClient client = mock(GoogleMapsGeocodingClient.class);
        given(client.geocode("Space Needle"))
                .willReturn(Optional.of(new GeocodedDestination("Space Needle", new Location(47.6205, -122.3493))));

        GoogleMapsDestinationGeocodingProvider provider = new GoogleMapsDestinationGeocodingProvider(client);

        Optional<GeocodedDestination> result = provider.geocode("Space Needle");

        assertTrue(result.isPresent());
        assertEquals("Space Needle", result.get().displayName());
    }
}
