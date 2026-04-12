package com.parksense.provider;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.integration.googlemaps.client.GoogleMapsPlacesClient;
import com.parksense.integration.googlemaps.dto.GoogleMapsPlace;
import com.parksense.integration.googlemaps.dto.GoogleMapsNearbySearchResponse;
import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleMapsParkingDataProvider implements ParkingDataProvider {

    private static final double DEFAULT_BASE_PRICE_PER_HOUR = 15.0;

    private final GoogleMapsPlacesClient googleMapsPlacesClient;
    private final MockParkingDataProvider mockParkingDataProvider;
    private final ParkingProviderProperties properties;

    public GoogleMapsParkingDataProvider(
            GoogleMapsPlacesClient googleMapsPlacesClient,
            MockParkingDataProvider mockParkingDataProvider,
            ParkingProviderProperties properties
    ) {
        this.googleMapsPlacesClient = googleMapsPlacesClient;
        this.mockParkingDataProvider = mockParkingDataProvider;
        this.properties = properties;
    }

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public List<ParkingSpot> findNearbySpots(Location destination) {
        try {
            GoogleMapsNearbySearchResponse response = googleMapsPlacesClient.searchNearbyParking(destination);
            List<ParkingSpot> mappedSpots = response.places() == null
                    ? List.of()
                    : response.places().stream()
                    .filter(this::isUsablePlace)
                    .map(this::toParkingSpot)
                    .toList();

            if (!mappedSpots.isEmpty()) {
                return mappedSpots;
            }
        } catch (RuntimeException exception) {
            if (!properties.isFallbackToMockOnFailure()) {
                throw exception;
            }
        }

        return mockParkingDataProvider.findNearbySpots(destination);
    }

    private ParkingSpot toParkingSpot(GoogleMapsPlace place) {
        String spotName = place.displayName() != null && place.displayName().text() != null
                ? place.displayName().text()
                : "Google Maps Parking Spot";

        return new ParkingSpot(
                place.id(),
                spotName,
                new Location(place.location().latitude(), place.location().longitude()),
                DEFAULT_BASE_PRICE_PER_HOUR
        );
    }

    private boolean isUsablePlace(GoogleMapsPlace place) {
        return place != null
                && place.id() != null
                && !place.id().isBlank()
                && place.location() != null
                && Double.isFinite(place.location().latitude())
                && Double.isFinite(place.location().longitude());
    }
}
