package com.parksense.provider;

import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MockParkingDataProvider implements ParkingDataProvider {

    private static final List<ParkingSpot> PARKING_SPOTS = List.of(
            new ParkingSpot("P1", "Central Garage", new Location(47.6097, -122.3331), "1200 4th Ave, Seattle, WA", "garage", 14.00),
            new ParkingSpot("P2", "Pine Street Lot", new Location(47.6122, -122.3355), "1520 Pine St, Seattle, WA", "lot", 11.50),
            new ParkingSpot("P3", "Waterfront Parking", new Location(47.6070, -122.3425), "1101 Alaskan Way, Seattle, WA", "waterfront", 18.00),
            new ParkingSpot("P4", "Convention Center Garage", new Location(47.6118, -122.3301), "705 Pike St, Seattle, WA", "garage", 16.50),
            new ParkingSpot("P5", "City Plaza Parking", new Location(47.6056, -122.3344), "601 5th Ave, Seattle, WA", "lot", 10.00)
    );

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public List<ParkingSpot> findNearbySpots(Location destination) {
        return PARKING_SPOTS;
    }
}
