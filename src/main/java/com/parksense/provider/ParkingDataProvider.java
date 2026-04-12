package com.parksense.provider;

import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;

import java.util.List;

public interface ParkingDataProvider {

    List<ParkingSpot> findNearbySpots(Location destination);
}
