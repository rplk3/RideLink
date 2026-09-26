package com.ridelink.account_service.exception;

public class SuspendedAccountException extends RuntimeException {

    public SuspendedAccountException(String message) {
        super(message);
    }
}