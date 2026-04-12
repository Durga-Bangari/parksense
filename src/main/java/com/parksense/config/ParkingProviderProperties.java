package com.parksense.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "parksense.provider")
public class ParkingProviderProperties {

    private String type = "mock";
    private String googleMapsApiKey = "";
    private int searchRadiusMeters = 1500;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGoogleMapsApiKey() {
        return googleMapsApiKey;
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
}
