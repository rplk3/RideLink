package com.ridelink.driver_vehicle_service.exception;

public class DuplicateRegistrationNumberException extends RuntimeException {
    public DuplicateRegistrationNumberException(String message) {
        super(message);
    }
}
