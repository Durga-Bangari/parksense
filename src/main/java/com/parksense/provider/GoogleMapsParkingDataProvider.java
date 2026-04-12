package com.parksense.provider;

import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;

import java.util.List;

public class GoogleMapsParkingDataProvider implements ParkingDataProvider {

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public List<ParkingSpot> findNearbySpots(Location destination) {
        throw new UnsupportedOperationException("Google Maps provider integration is not implemented yet");
    }
}
