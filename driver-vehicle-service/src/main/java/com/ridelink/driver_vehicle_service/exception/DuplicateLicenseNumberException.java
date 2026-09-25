package com.ridelink.driver_vehicle_service.exception;

public class DuplicateLicenseNumberException extends RuntimeException {
    public DuplicateLicenseNumberException(String message) {
        super(message);
    }
}
