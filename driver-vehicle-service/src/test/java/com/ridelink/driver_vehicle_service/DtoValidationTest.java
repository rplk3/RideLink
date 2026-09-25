package com.ridelink.driver_vehicle_service;

import com.ridelink.driver_vehicle_service.dto.request.AvailabilityUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverCreateRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationRequest;
import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.enums.AvailabilityStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DTO validation constraints.
 * No Spring context or MongoDB required — uses the Jakarta Validator directly.
 */
class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private LocationRequest validLocation() {
        return new LocationRequest(12.9716, 77.5946); // Bengaluru
    }

    private DriverCreateRequest validDriverCreateRequest() {
        return new DriverCreateRequest("acc-001", "KA01AB1234", "Bengaluru", validLocation());
    }

    private VehicleRegisterRequest validVehicleRegisterRequest() {
        return new VehicleRegisterRequest("KA01AB1234", "Toyota", "Innova", "SUV", "White", 2020);
    }

    // -------------------------------------------------------------------------
    // DriverCreateRequest
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid DriverCreateRequest passes validation")
    void validDriverCreateRequest_passesValidation() {
        Set<ConstraintViolation<DriverCreateRequest>> violations =
                validator.validate(validDriverCreateRequest());
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Blank accountId fails validation")
    void blankAccountId_failsValidation() {
        DriverCreateRequest req = validDriverCreateRequest();
        req.setAccountId("  ");
        Set<ConstraintViolation<DriverCreateRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("accountId"));
    }

    @Test
    @DisplayName("Blank licenseNumber fails validation")
    void blankLicenseNumber_failsValidation() {
        DriverCreateRequest req = validDriverCreateRequest();
        req.setLicenseNumber("");
        Set<ConstraintViolation<DriverCreateRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("licenseNumber"));
    }

    @Test
    @DisplayName("Blank serviceArea fails validation")
    void blankServiceArea_failsValidation() {
        DriverCreateRequest req = validDriverCreateRequest();
        req.setServiceArea("   ");
        Set<ConstraintViolation<DriverCreateRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("serviceArea"));
    }

    // -------------------------------------------------------------------------
    // LocationRequest — latitude bounds
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Latitude below -90 fails validation")
    void latitudeBelowMinus90_failsValidation() {
        LocationRequest location = new LocationRequest(-90.001, 77.5946);
        Set<ConstraintViolation<LocationRequest>> violations = validator.validate(location);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("latitude"));
    }

    @Test
    @DisplayName("Latitude above 90 fails validation")
    void latitudeAbove90_failsValidation() {
        LocationRequest location = new LocationRequest(90.001, 77.5946);
        Set<ConstraintViolation<LocationRequest>> violations = validator.validate(location);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("latitude"));
    }

    @Test
    @DisplayName("Latitude at boundary values passes validation")
    void latitudeAtBoundaries_passesValidation() {
        assertThat(validator.validate(new LocationRequest(-90.0, 0.0))).isEmpty();
        assertThat(validator.validate(new LocationRequest(90.0, 0.0))).isEmpty();
    }

    // -------------------------------------------------------------------------
    // LocationRequest — longitude bounds
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Longitude below -180 fails validation")
    void longitudeBelowMinus180_failsValidation() {
        LocationRequest location = new LocationRequest(12.9716, -180.001);
        Set<ConstraintViolation<LocationRequest>> violations = validator.validate(location);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("longitude"));
    }

    @Test
    @DisplayName("Longitude above 180 fails validation")
    void longitudeAbove180_failsValidation() {
        LocationRequest location = new LocationRequest(12.9716, 180.001);
        Set<ConstraintViolation<LocationRequest>> violations = validator.validate(location);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("longitude"));
    }

    @Test
    @DisplayName("Longitude at boundary values passes validation")
    void longitudeAtBoundaries_passesValidation() {
        assertThat(validator.validate(new LocationRequest(0.0, -180.0))).isEmpty();
        assertThat(validator.validate(new LocationRequest(0.0, 180.0))).isEmpty();
    }

    // -------------------------------------------------------------------------
    // Cascaded validation — null currentLocation in DriverCreateRequest
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Null currentLocation in DriverCreateRequest fails validation")
    void nullCurrentLocation_failsValidation() {
        DriverCreateRequest req = validDriverCreateRequest();
        req.setCurrentLocation(null);
        Set<ConstraintViolation<DriverCreateRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("currentLocation"));
    }

    @Test
    @DisplayName("Invalid nested LocationRequest in DriverCreateRequest fails cascaded validation")
    void invalidNestedLocation_failsCascadedValidation() {
        DriverCreateRequest req = new DriverCreateRequest(
                "acc-001", "KA01AB1234", "Bengaluru",
                new LocationRequest(91.0, 0.0) // latitude out of range
        );
        Set<ConstraintViolation<DriverCreateRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(
                v -> v.getPropertyPath().toString().equals("currentLocation.latitude"));
    }

    // -------------------------------------------------------------------------
    // AvailabilityUpdateRequest
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Null availabilityStatus fails validation")
    void nullAvailabilityStatus_failsValidation() {
        AvailabilityUpdateRequest req = new AvailabilityUpdateRequest(null);
        Set<ConstraintViolation<AvailabilityUpdateRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(
                v -> v.getPropertyPath().toString().equals("availabilityStatus"));
    }

    @Test
    @DisplayName("Valid AvailabilityUpdateRequest passes validation")
    void validAvailabilityUpdateRequest_passesValidation() {
        AvailabilityUpdateRequest req = new AvailabilityUpdateRequest(AvailabilityStatus.AVAILABLE);
        assertThat(validator.validate(req)).isEmpty();
    }

    // -------------------------------------------------------------------------
    // VehicleRegisterRequest
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Valid VehicleRegisterRequest passes validation")
    void validVehicleRegisterRequest_passesValidation() {
        Set<ConstraintViolation<VehicleRegisterRequest>> violations =
                validator.validate(validVehicleRegisterRequest());
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Blank registrationNumber fails validation")
    void blankRegistrationNumber_failsValidation() {
        VehicleRegisterRequest req = validVehicleRegisterRequest();
        req.setRegistrationNumber("  ");
        Set<ConstraintViolation<VehicleRegisterRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(
                v -> v.getPropertyPath().toString().equals("registrationNumber"));
    }

    @Test
    @DisplayName("Year before 1886 fails validation")
    void yearBefore1886_failsValidation() {
        VehicleRegisterRequest req = validVehicleRegisterRequest();
        req.setYear(1885);
        Set<ConstraintViolation<VehicleRegisterRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("year"));
    }

    @Test
    @DisplayName("Null year fails validation")
    void nullYear_failsValidation() {
        VehicleRegisterRequest req = validVehicleRegisterRequest();
        req.setYear(null);
        Set<ConstraintViolation<VehicleRegisterRequest>> violations = validator.validate(req);
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("year"));
    }
}
