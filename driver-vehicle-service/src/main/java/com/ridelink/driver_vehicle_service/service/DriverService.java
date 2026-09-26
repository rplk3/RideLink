package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.request.AvailabilityUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverCreateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.DriverResponse;
import com.ridelink.driver_vehicle_service.dto.response.EligibleDriverResponse;

import java.util.List;

public interface DriverService {

    DriverResponse createDriver(DriverCreateRequest request);

    DriverResponse getDriverById(String id);

    DriverResponse updateDriverProfile(String id, DriverUpdateRequest request);

    DriverResponse updateDriverAvailability(String id, AvailabilityUpdateRequest request);

    DriverResponse updateDriverLocation(String id, LocationUpdateRequest request);

    List<EligibleDriverResponse> getEligibleDrivers(String serviceArea);
}
