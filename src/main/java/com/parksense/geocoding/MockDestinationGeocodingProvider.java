package com.parksense.geocoding;

import com.parksense.model.GeocodedDestination;
import com.parksense.model.Location;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class MockDestinationGeocodingProvider implements DestinationGeocodingProvider {

    private static final Map<String, GeocodedDestination> KNOWN_DESTINATIONS = Map.of(
            "seattle convention center",
            new GeocodedDestination("Seattle Convention Center", new Location(47.6114, -122.3317)),
            "pike place market",
            new GeocodedDestination("Pike Place Market", new Location(47.6094, -122.3422)),
            "space needle",
            new GeocodedDestination("Space Needle", new Location(47.6205, -122.3493)),
            "seattle waterfront",
            new GeocodedDestination("Seattle Waterfront", new Location(47.6073, -122.3425))
    );

    @Override
    public String getProviderType() {
        return "mock";
    }

    @Override
    public Optional<GeocodedDestination> geocode(String destinationQuery) {
        if (destinationQuery == null || destinationQuery.isBlank()) {
            return Optional.empty();
        }

        return Optional.ofNullable(KNOWN_DESTINATIONS.get(normalize(destinationQuery)));
    }

    private String normalize(String destinationQuery) {
        return destinationQuery.trim().toLowerCase();
    }
}
