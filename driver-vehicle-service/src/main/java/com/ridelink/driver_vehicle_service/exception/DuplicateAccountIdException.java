package com.ridelink.driver_vehicle_service.exception;

public class DuplicateAccountIdException extends RuntimeException {
    public DuplicateAccountIdException(String message) {
        super(message);
    }
}
