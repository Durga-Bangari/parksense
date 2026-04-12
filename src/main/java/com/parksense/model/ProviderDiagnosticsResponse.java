package com.parksense.model;

public record ProviderDiagnosticsResponse(
        String configuredProviderType,
        String activeProviderType,
        boolean providerReady,
        String statusMessage,
        boolean fallbackToMockOnFailure,
        int searchRadiusMeters
) {
}
