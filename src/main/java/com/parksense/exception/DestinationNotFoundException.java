package com.parksense.exception;

public class DestinationNotFoundException extends RuntimeException {

    public DestinationNotFoundException(String destination) {
        super("Destination could not be resolved: " + destination);
    }
}
