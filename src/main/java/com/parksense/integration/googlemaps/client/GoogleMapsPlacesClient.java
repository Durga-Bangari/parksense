package com.parksense.integration.googlemaps.client;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.integration.googlemaps.dto.GoogleMapsCircle;
import com.parksense.integration.googlemaps.dto.GoogleMapsLatLng;
import com.parksense.integration.googlemaps.dto.GoogleMapsLocationRestriction;
import com.parksense.integration.googlemaps.dto.GoogleMapsNearbySearchRequest;
import com.parksense.integration.googlemaps.dto.GoogleMapsNearbySearchResponse;
import com.parksense.model.Location;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GoogleMapsPlacesClient {

    private static final String NEARBY_SEARCH_PATH = "/places:searchNearby";
    private static final String FIELD_MASK =
            "places.id,places.displayName,places.location,places.formattedAddress,places.primaryType";

    private final RestClient restClient;
    private final ParkingProviderProperties properties;

    public GoogleMapsPlacesClient(RestClient.Builder restClientBuilder, ParkingProviderProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.getGoogleMapsBaseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.properties = properties;
    }

    public GoogleMapsNearbySearchResponse searchNearbyParking(Location destination) {
        validateApiKey();

        GoogleMapsNearbySearchRequest request = createNearbySearchRequest(destination);
        GoogleMapsNearbySearchResponse response = restClient.post()
                .uri(NEARBY_SEARCH_PATH)
                .header("X-Goog-Api-Key", properties.getGoogleMapsApiKey())
                .header("X-Goog-FieldMask", FIELD_MASK)
                .body(request)
                .retrieve()
                .body(GoogleMapsNearbySearchResponse.class);

        return response != null ? response : new GoogleMapsNearbySearchResponse(List.of());
    }

    GoogleMapsNearbySearchRequest createNearbySearchRequest(Location destination) {
        return new GoogleMapsNearbySearchRequest(
                List.of("parking"),
                10,
                new GoogleMapsLocationRestriction(
                        new GoogleMapsCircle(
                                new GoogleMapsLatLng(destination.latitude(), destination.longitude()),
                                properties.getSearchRadiusMeters()
                        )
                ),
                "DISTANCE"
        );
    }

    private void validateApiKey() {
        if (properties.getGoogleMapsApiKey() == null || properties.getGoogleMapsApiKey().isBlank()) {
            throw new IllegalStateException("Google Maps API key is required when provider type is google-maps");
        }
    }
}
