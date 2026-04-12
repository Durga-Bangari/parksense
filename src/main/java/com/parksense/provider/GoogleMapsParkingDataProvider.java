package com.parksense.provider;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ExternalProviderException;
import com.parksense.exception.ProviderConfigurationException;
import com.parksense.integration.googlemaps.client.GoogleMapsPlacesClient;
import com.parksense.integration.googlemaps.dto.GoogleMapsPlace;
import com.parksense.integration.googlemaps.dto.GoogleMapsNearbySearchResponse;
import com.parksense.model.Location;
import com.parksense.model.ParkingSpot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GoogleMapsParkingDataProvider implements ParkingDataProvider {

    private static final double DEFAULT_BASE_PRICE_PER_HOUR = 15.0;

    private final GoogleMapsPlacesClient googleMapsPlacesClient;
    private final MockParkingDataProvider mockParkingDataProvider;
    private final ParkingProviderProperties properties;
    private final Clock clock;
    private final Map<String, CacheEntry> cachedSpotsByDestination;

    @Autowired
    public GoogleMapsParkingDataProvider(
            GoogleMapsPlacesClient googleMapsPlacesClient,
            MockParkingDataProvider mockParkingDataProvider,
            ParkingProviderProperties properties
    ) {
        this(googleMapsPlacesClient, mockParkingDataProvider, properties, Clock.systemUTC());
    }

    GoogleMapsParkingDataProvider(
            GoogleMapsPlacesClient googleMapsPlacesClient,
            MockParkingDataProvider mockParkingDataProvider,
            ParkingProviderProperties properties,
            Clock clock
    ) {
        this.googleMapsPlacesClient = googleMapsPlacesClient;
        this.mockParkingDataProvider = mockParkingDataProvider;
        this.properties = properties;
        this.clock = clock;
        this.cachedSpotsByDestination = new ConcurrentHashMap<>();
    }

    @Override
    public String getProviderType() {
        return "google-maps";
    }

    @Override
    public List<ParkingSpot> findNearbySpots(Location destination) {
        List<ParkingSpot> cachedSpots = getCachedSpots(destination);
        if (cachedSpots != null) {
            return cachedSpots;
        }

        try {
            GoogleMapsNearbySearchResponse response = googleMapsPlacesClient.searchNearbyParking(destination);
            List<ParkingSpot> mappedSpots = response.places() == null
                    ? List.of()
                    : response.places().stream()
                    .filter(this::isUsablePlace)
                    .map(this::toParkingSpot)
                    .toList();

            if (!mappedSpots.isEmpty()) {
                cacheSpots(destination, mappedSpots);
                return mappedSpots;
            }
        } catch (ProviderConfigurationException | ExternalProviderException exception) {
            if (!properties.isFallbackToMockOnFailure()) {
                throw exception;
            }
        } catch (RuntimeException exception) {
            if (!properties.isFallbackToMockOnFailure()) {
                throw new ExternalProviderException(
                        getProviderType(),
                        "Google Maps provider failed while fetching nearby parking data",
                        exception
                );
            }
        }

        return mockParkingDataProvider.findNearbySpots(destination);
    }

    private List<ParkingSpot> getCachedSpots(Location destination) {
        if (!properties.isCacheEnabled()) {
            return null;
        }

        String cacheKey = buildCacheKey(destination);
        CacheEntry cacheEntry = cachedSpotsByDestination.get(cacheKey);

        if (cacheEntry == null) {
            return null;
        }

        if (cacheEntry.expiresAt().isAfter(clock.instant())) {
            return cacheEntry.spots();
        }

        cachedSpotsByDestination.remove(cacheKey);
        return null;
    }

    private void cacheSpots(Location destination, List<ParkingSpot> spots) {
        if (!properties.isCacheEnabled()) {
            return;
        }

        Instant expiresAt = clock.instant().plusSeconds(Math.max(1, properties.getCacheTtlSeconds()));
        cachedSpotsByDestination.put(buildCacheKey(destination), new CacheEntry(List.copyOf(spots), expiresAt));
    }

    private String buildCacheKey(Location destination) {
        return "%.5f:%.5f".formatted(destination.latitude(), destination.longitude());
    }

    private ParkingSpot toParkingSpot(GoogleMapsPlace place) {
        String spotName = place.displayName() != null && place.displayName().text() != null
                ? place.displayName().text()
                : "Google Maps Parking Spot";

        return new ParkingSpot(
                place.id(),
                spotName,
                new Location(place.location().latitude(), place.location().longitude()),
                place.formattedAddress(),
                place.primaryType(),
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

    private record CacheEntry(List<ParkingSpot> spots, Instant expiresAt) {
    }
}
