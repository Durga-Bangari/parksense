package com.parksense.repository;

import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import com.parksense.provider.ParkingDataProvider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ParkingSpotRepository implements ParkingDataProvider {

    private static final List<ParkingSpot> PARKING_SPOTS = List.of(
            new ParkingSpot("P1", "Central Garage", new Location(47.6097, -122.3331), 14.00),
            new ParkingSpot("P2", "Pine Street Lot", new Location(47.6122, -122.3355), 11.50),
            new ParkingSpot("P3", "Waterfront Parking", new Location(47.6070, -122.3425), 18.00),
            new ParkingSpot("P4", "Convention Center Garage", new Location(47.6118, -122.3301), 16.50),
            new ParkingSpot("P5", "City Plaza Parking", new Location(47.6056, -122.3344), 10.00)
    );

    @Override
    public List<ParkingSpot> findNearbySpots(Location destination) {
        return PARKING_SPOTS;
    }
}
