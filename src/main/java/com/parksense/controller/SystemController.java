package com.parksense.controller;

import com.parksense.config.ParkingProviderProperties;
import com.parksense.model.ProviderDiagnosticsResponse;
import com.parksense.provider.ParkingDataProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SystemController {

    private final ParkingDataProvider parkingDataProvider;
    private final ParkingProviderProperties parkingProviderProperties;

    public SystemController(
            ParkingDataProvider parkingDataProvider,
            ParkingProviderProperties parkingProviderProperties
    ) {
        this.parkingDataProvider = parkingDataProvider;
        this.parkingProviderProperties = parkingProviderProperties;
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
