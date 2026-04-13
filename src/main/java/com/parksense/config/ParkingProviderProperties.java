package com.parksense.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "parksense.provider")
public class ParkingProviderProperties {

    private String type = "mock";
    private String geocodingType = "mock";
    private String googleMapsBaseUrl = "https://places.googleapis.com/v1";
    private String googleMapsGeocodingBaseUrl = "https://maps.googleapis.com/maps/api/geocode";
    private String googleMapsApiKey = "";
    private int searchRadiusMeters = 1500;
    private boolean fallbackToMockOnFailure = true;
    private boolean cacheEnabled = true;
    private int cacheTtlSeconds = 300;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGeocodingType() {
        return geocodingType;
    }

    public void setGeocodingType(String geocodingType) {
        this.geocodingType = geocodingType;
    }

    public String getGoogleMapsApiKey() {
        return googleMapsApiKey;
    }

    public String getGoogleMapsBaseUrl() {
        return googleMapsBaseUrl;
    }

    public void setGoogleMapsBaseUrl(String googleMapsBaseUrl) {
        this.googleMapsBaseUrl = googleMapsBaseUrl;
    }

    public String getGoogleMapsGeocodingBaseUrl() {
        return googleMapsGeocodingBaseUrl;
    }

    public void setGoogleMapsGeocodingBaseUrl(String googleMapsGeocodingBaseUrl) {
        this.googleMapsGeocodingBaseUrl = googleMapsGeocodingBaseUrl;
    }

    public void setGoogleMapsApiKey(String googleMapsApiKey) {
        this.googleMapsApiKey = googleMapsApiKey;
    }

    public int getSearchRadiusMeters() {
        return searchRadiusMeters;
    }

    public void setSearchRadiusMeters(int searchRadiusMeters) {
        this.searchRadiusMeters = searchRadiusMeters;
    }

    public boolean isFallbackToMockOnFailure() {
        return fallbackToMockOnFailure;
    }

    public void setFallbackToMockOnFailure(boolean fallbackToMockOnFailure) {
        this.fallbackToMockOnFailure = fallbackToMockOnFailure;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public int getCacheTtlSeconds() {
        return cacheTtlSeconds;
    }

    public void setCacheTtlSeconds(int cacheTtlSeconds) {
        this.cacheTtlSeconds = cacheTtlSeconds;
    }
}
