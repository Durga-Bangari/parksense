package com.parksense.util;

import com.parksense.model.Location;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DistanceCalculatorTest {

    @Test
    void returnsZeroWhenLocationsAreIdentical() {
        Location location = new Location(47.6097, -122.3331);

        double distanceMeters = DistanceCalculator.calculateDistanceMeters(location, location);

        assertEquals(0.0, distanceMeters, 0.001);
    }

    @Test
    void calculatesReasonableDistanceBetweenTwoSeattleLocations() {
        Location origin = new Location(47.6097, -122.3331);
        Location destination = new Location(47.6122, -122.3355);

        double distanceMeters = DistanceCalculator.calculateDistanceMeters(origin, destination);

        assertTrue(distanceMeters > 300);
        assertTrue(distanceMeters < 400);
    }
}
