package com.parksense.geocoding;

import com.parksense.model.GeocodedDestination;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MockDestinationGeocodingProviderTest {

    private final MockDestinationGeocodingProvider provider = new MockDestinationGeocodingProvider();

    @Test
    void geocodesKnownDestinationIgnoringCaseAndWhitespace() {
        Optional<GeocodedDestination> result = provider.geocode("  Space Needle  ");

        assertTrue(result.isPresent());
        assertEquals("Space Needle", result.get().displayName());
        assertEquals(47.6205, result.get().location().latitude());
        assertEquals(-122.3493, result.get().location().longitude());
    }

    @Test
    void returnsEmptyForUnknownDestination() {
        Optional<GeocodedDestination> result = provider.geocode("Unknown Place");

        assertTrue(result.isEmpty());
    }
}
