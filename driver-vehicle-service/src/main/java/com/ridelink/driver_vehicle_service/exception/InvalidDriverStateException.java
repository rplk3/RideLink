package com.ridelink.driver_vehicle_service.exception;

public class InvalidDriverStateException extends RuntimeException {
    public InvalidDriverStateException(String message) {
        super(message);
    }
}
