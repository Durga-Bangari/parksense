package com.parksense.provider;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ExternalProviderException;
import com.parksense.integration.googlemaps.client.GoogleMapsPlacesClient;
import com.parksense.integration.googlemaps.dto.GoogleMapsDisplayName;
import com.parksense.integration.googlemaps.dto.GoogleMapsLatLng;
import com.parksense.integration.googlemaps.dto.GoogleMapsNearbySearchResponse;
import com.parksense.integration.googlemaps.dto.GoogleMapsPlace;
import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class GoogleMapsParkingDataProviderTest {

    private final GoogleMapsPlacesClient googleMapsPlacesClient = mock(GoogleMapsPlacesClient.class);
    private final MockParkingDataProvider mockParkingDataProvider = new MockParkingDataProvider();
    private final ParkingProviderProperties properties = new ParkingProviderProperties();
    private final GoogleMapsParkingDataProvider googleMapsParkingDataProvider =
            new GoogleMapsParkingDataProvider(googleMapsPlacesClient, mockParkingDataProvider, properties);

    @Test
    void mapsValidGooglePlacesIntoParkingSpots() {
        Location destination = new Location(47.6097, -122.3331);
        GoogleMapsPlace place = new GoogleMapsPlace(
                "gm-1",
                new GoogleMapsDisplayName("Downtown Parking", "en"),
                new GoogleMapsLatLng(47.6100, -122.3340),
                "123 Pine St, Seattle, WA",
                "parking"
        );
        given(googleMapsPlacesClient.searchNearbyParking(destination))
                .willReturn(new GoogleMapsNearbySearchResponse(List.of(place)));

        List<ParkingSpot> spots = googleMapsParkingDataProvider.findNearbySpots(destination);

        assertEquals(1, spots.size());
        assertEquals("gm-1", spots.get(0).id());
        assertEquals("Downtown Parking", spots.get(0).name());
        assertEquals("123 Pine St, Seattle, WA", spots.get(0).address());
        assertEquals("parking", spots.get(0).category());
    }

    @Test
    void reusesCachedGoogleMapsResultsForRepeatedRequests() {
        Location destination = new Location(47.6097, -122.3331);
        GoogleMapsPlace place = new GoogleMapsPlace(
                "gm-1",
                new GoogleMapsDisplayName("Downtown Parking", "en"),
                new GoogleMapsLatLng(47.6100, -122.3340),
                "123 Pine St, Seattle, WA",
                "parking"
        );
        given(googleMapsPlacesClient.searchNearbyParking(destination))
                .willReturn(new GoogleMapsNearbySearchResponse(List.of(place)));

        googleMapsParkingDataProvider.findNearbySpots(destination);
        googleMapsParkingDataProvider.findNearbySpots(destination);

        verify(googleMapsPlacesClient, times(1)).searchNearbyParking(destination);
    }

    @Test
    void refreshesCachedResultsAfterTtlExpires() {
        Location destination = new Location(47.6097, -122.3331);
        properties.setCacheTtlSeconds(60);
        MutableClock mutableClock = new MutableClock(Instant.parse("2026-04-12T16:00:00Z"));
        GoogleMapsParkingDataProvider cachingProvider = new GoogleMapsParkingDataProvider(
                googleMapsPlacesClient,
                mockParkingDataProvider,
                properties,
                mutableClock
        );
        GoogleMapsPlace place = new GoogleMapsPlace(
                "gm-1",
                new GoogleMapsDisplayName("Downtown Parking", "en"),
                new GoogleMapsLatLng(47.6100, -122.3340),
                "123 Pine St, Seattle, WA",
                "parking"
        );
        given(googleMapsPlacesClient.searchNearbyParking(destination))
                .willReturn(new GoogleMapsNearbySearchResponse(List.of(place)));

        cachingProvider.findNearbySpots(destination);
        mutableClock.advanceSeconds(61);
        cachingProvider.findNearbySpots(destination);

        verify(googleMapsPlacesClient, times(2)).searchNearbyParking(destination);
    }

    @Test
    void fallsBackToMockDataWhenGoogleMapsCallFails() {
        Location destination = new Location(47.6097, -122.3331);
        given(googleMapsPlacesClient.searchNearbyParking(destination))
                .willThrow(new IllegalStateException("missing key"));

        List<ParkingSpot> spots = googleMapsParkingDataProvider.findNearbySpots(destination);

        assertEquals(5, spots.size());
    }

    @Test
    void filtersIncompleteGooglePlacesAndFallsBackWhenNothingUsableRemains() {
        Location destination = new Location(47.6097, -122.3331);
        GoogleMapsPlace incompletePlace = new GoogleMapsPlace(
                "",
                new GoogleMapsDisplayName("Unnamed", "en"),
                null,
                null,
                "parking"
        );
        given(googleMapsPlacesClient.searchNearbyParking(destination))
                .willReturn(new GoogleMapsNearbySearchResponse(List.of(incompletePlace)));

        List<ParkingSpot> spots = googleMapsParkingDataProvider.findNearbySpots(destination);

        assertEquals(5, spots.size());
    }

    @Test
    void rethrowsProviderFailureWhenFallbackIsDisabled() {
        Location destination = new Location(47.6097, -122.3331);
        properties.setFallbackToMockOnFailure(false);
        given(googleMapsPlacesClient.searchNearbyParking(destination))
                .willThrow(new IllegalStateException("provider unavailable"));

        assertThrows(
                ExternalProviderException.class,
                () -> googleMapsParkingDataProvider.findNearbySpots(destination)
        );
    }

    private static final class MutableClock extends Clock {

        private Instant currentInstant;

        private MutableClock(Instant currentInstant) {
            this.currentInstant = currentInstant;
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return currentInstant;
        }

        private void advanceSeconds(long seconds) {
            currentInstant = currentInstant.plusSeconds(seconds);
        }
    }
}
