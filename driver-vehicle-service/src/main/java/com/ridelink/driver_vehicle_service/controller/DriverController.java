package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.dto.request.AvailabilityUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverCreateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.DriverResponse;
import com.ridelink.driver_vehicle_service.dto.response.EligibleDriverResponse;
import com.ridelink.driver_vehicle_service.service.DriverService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Validated
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DriverResponse createDriver(@Valid @RequestBody DriverCreateRequest request) {
        return driverService.createDriver(request);
    }

    @GetMapping("/{driverId}")
    public DriverResponse getDriverById(@PathVariable String driverId) {
        return driverService.getDriverById(driverId);
    }

    @PutMapping("/{driverId}")
    public DriverResponse updateDriverProfile(
            @PathVariable String driverId,
            @Valid @RequestBody DriverUpdateRequest request) {
        return driverService.updateDriverProfile(driverId, request);
    }

    @PatchMapping("/{driverId}/availability")
    public DriverResponse updateDriverAvailability(
            @PathVariable String driverId,
            @Valid @RequestBody AvailabilityUpdateRequest request) {
        return driverService.updateDriverAvailability(driverId, request);
    }

    @PatchMapping("/{driverId}/location")
    public DriverResponse updateDriverLocation(
            @PathVariable String driverId,
            @Valid @RequestBody LocationUpdateRequest request) {
        return driverService.updateDriverLocation(driverId, request);
    }

    @GetMapping("/eligible")
    public List<EligibleDriverResponse> getEligibleDrivers(
            @RequestParam("serviceArea") @NotBlank String serviceArea) {
        return driverService.getEligibleDrivers(serviceArea);
    }
}
