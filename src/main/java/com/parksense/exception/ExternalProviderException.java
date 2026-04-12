package com.parksense.exception;

public class ExternalProviderException extends RuntimeException {

    private final String providerType;

    public ExternalProviderException(String providerType, String message) {
        super(message);
        this.providerType = providerType;
    }

    public ExternalProviderException(String providerType, String message, Throwable cause) {
        super(message, cause);
        this.providerType = providerType;
    }

    public String getProviderType() {
        return providerType;
    }
}
