package com.parksense.exception;

public class ProviderConfigurationException extends RuntimeException {

    private final String providerType;

    public ProviderConfigurationException(String providerType, String message) {
        super(message);
        this.providerType = providerType;
    }

    public String getProviderType() {
        return providerType;
    }
}
