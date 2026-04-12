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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

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
}
