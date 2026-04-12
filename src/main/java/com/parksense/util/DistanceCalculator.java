package com.parksense.util;

import com.parksense.model.Location;

public final class DistanceCalculator {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private DistanceCalculator() {
    }

    public static double calculateDistanceMeters(Location origin, Location destination) {
        double latitudeDeltaRadians = Math.toRadians(destination.latitude() - origin.latitude());
        double longitudeDeltaRadians = Math.toRadians(destination.longitude() - origin.longitude());

        double originLatitudeRadians = Math.toRadians(origin.latitude());
        double destinationLatitudeRadians = Math.toRadians(destination.latitude());

        double a = Math.sin(latitudeDeltaRadians / 2) * Math.sin(latitudeDeltaRadians / 2)
                + Math.cos(originLatitudeRadians) * Math.cos(destinationLatitudeRadians)
                * Math.sin(longitudeDeltaRadians / 2) * Math.sin(longitudeDeltaRadians / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_METERS * c;
    }
}
