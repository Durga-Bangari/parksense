package com.parksense.integration.googlemaps.client;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.exception.ExternalProviderException;
import com.parksense.exception.ProviderConfigurationException;
import com.parksense.integration.googlemaps.dto.GoogleMapsGeocodingResponse;
import com.parksense.integration.googlemaps.dto.GoogleMapsGeocodingResult;
import com.parksense.integration.googlemaps.dto.GoogleMapsGeometry;
import com.parksense.model.GeocodedDestination;
import com.parksense.model.Location;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Component
public class GoogleMapsGeocodingClient {

    private static final String GEOCODING_PATH = "/json";

    private final RestClient restClient;
    private final ParkingProviderProperties properties;

    public GoogleMapsGeocodingClient(RestClient.Builder restClientBuilder, ParkingProviderProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.getGoogleMapsGeocodingBaseUrl())
                .build();
        this.properties = properties;
    }

    public Optional<GeocodedDestination> geocode(String destinationQuery) {
        validateApiKey();

        GoogleMapsGeocodingResponse response;
        try {
            response = restClient.get()
                    .uri(createGeocodingPath(destinationQuery))
                    .retrieve()
                    .body(GoogleMapsGeocodingResponse.class);
        } catch (RestClientException exception) {
            throw new ExternalProviderException(
                    "google-maps",
                    "Google Maps geocoding request failed",
                    exception
            );
        }

        return mapResponse(response);
    }

    Optional<GeocodedDestination> mapResponse(GoogleMapsGeocodingResponse response) {
        List<GoogleMapsGeocodingResult> results = response != null && response.results() != null
                ? response.results()
                : List.of();

        return results.stream()
                .filter(this::isUsableResult)
                .findFirst()
                .map(result -> new GeocodedDestination(
                        result.formatted_address(),
                        new Location(
                                result.geometry().location().latitude(),
                                result.geometry().location().longitude()
                        )
                ));
    }

    String createGeocodingPath(String destinationQuery) {
        return UriComponentsBuilder.fromPath(GEOCODING_PATH)
                .queryParam("address", destinationQuery)
                .queryParam("key", properties.getGoogleMapsApiKey())
                .build()
                .toUriString();
    }

    private boolean isUsableResult(GoogleMapsGeocodingResult result) {
        GoogleMapsGeometry geometry = result.geometry();
        return result.formatted_address() != null
                && !result.formatted_address().isBlank()
                && geometry != null
                && geometry.location() != null
                && Double.isFinite(geometry.location().latitude())
                && Double.isFinite(geometry.location().longitude());
    }

    private void validateApiKey() {
        if (properties.getGoogleMapsApiKey() == null || properties.getGoogleMapsApiKey().isBlank()) {
            throw new ProviderConfigurationException(
                    "google-maps",
                    "Google Maps API key is required when geocoding provider type is google-maps"
            );
        }
    }
}
