package com.parksense.controller;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.model.ProviderDiagnosticsResponse;
import com.parksense.model.SearchHistoryItem;
import com.parksense.provider.ParkingDataProvider;
import com.parksense.service.SearchHistoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SystemController {

    private final ParkingDataProvider parkingDataProvider;
    private final ParkingProviderProperties parkingProviderProperties;
    private final SearchHistoryService searchHistoryService;

    public SystemController(
            ParkingDataProvider parkingDataProvider,
            ParkingProviderProperties parkingProviderProperties,
            SearchHistoryService searchHistoryService
    ) {
        this.parkingDataProvider = parkingDataProvider;
        this.parkingProviderProperties = parkingProviderProperties;
        this.searchHistoryService = searchHistoryService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
                "status", "UP",
                "application", "ParkSense"
        );
    }

    @GetMapping("/provider")
    public ProviderDiagnosticsResponse providerDiagnostics() {
        boolean providerReady = isProviderReady();

        return new ProviderDiagnosticsResponse(
                parkingProviderProperties.getType(),
                parkingDataProvider.getProviderType(),
                providerReady,
                buildStatusMessage(providerReady),
                parkingProviderProperties.isFallbackToMockOnFailure(),
                parkingProviderProperties.getSearchRadiusMeters()
        );
    }

    @GetMapping("/search-history")
    public java.util.List<SearchHistoryItem> recentSearchHistory(
            @RequestParam(defaultValue = "10") int limit
    ) {
        return searchHistoryService.getRecentSearches(limit);
    }

    private boolean isProviderReady() {
        if ("google-maps".equals(parkingDataProvider.getProviderType())) {
            return parkingProviderProperties.getGoogleMapsApiKey() != null
                    && !parkingProviderProperties.getGoogleMapsApiKey().isBlank();
        }

        return true;
    }

    private String buildStatusMessage(boolean providerReady) {
        if (providerReady) {
            return "Provider configuration is ready";
        }

        return "Provider configuration is incomplete";
    }
}
