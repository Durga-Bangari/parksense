package com.parksense.provider;

import com.parksense.integration.googlemaps.client.GoogleMapsPlacesClient;
import com.parksense.integration.googlemaps.dto.GoogleMapsPlace;
import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleMapsParkingDataProvider implements ParkingDataProvider {

    private static final double DEFAULT_BASE_PRICE_PER_HOUR = 15.0;

    private final GoogleMapsPlacesClient googleMapsPlacesClient;

    public GoogleMapsParkingDataProvider(GoogleMapsPlacesClient googleMapsPlacesClient) {
        this.googleMapsPlacesClient = googleMapsPlacesClient;
    }

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public List<ParkingSpot> findNearbySpots(Location destination) {
        return googleMapsPlacesClient.searchNearbyParking(destination).places().stream()
                .map(this::toParkingSpot)
                .toList();
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
}
