package com.ridelink.driver_vehicle_service.mapper;

import com.ridelink.driver_vehicle_service.dto.request.LocationRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.DriverResponse;
import com.ridelink.driver_vehicle_service.dto.response.EligibleDriverResponse;
import com.ridelink.driver_vehicle_service.dto.response.LocationResponse;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.Location;

public class DriverMapper {

    public static Location toLocation(LocationRequest request) {
        if (request == null) return null;
        return Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public static Location toLocation(LocationUpdateRequest request) {
        if (request == null) return null;
        return Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public static LocationResponse toLocationResponse(Location location) {
        if (location == null) return null;
        return LocationResponse.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }

    public static DriverResponse toDriverResponse(Driver driver) {
        if (driver == null) return null;
        return DriverResponse.builder()
                .id(driver.getId())
                .accountId(driver.getAccountId())
                .licenseNumber(driver.getLicenseNumber())
                .availabilityStatus(driver.getAvailabilityStatus())
                .serviceArea(driver.getServiceArea())
                .currentLocation(toLocationResponse(driver.getCurrentLocation()))
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }

    public static EligibleDriverResponse toEligibleDriverResponse(Driver driver) {
        if (driver == null) return null;
        return EligibleDriverResponse.builder()
                .driverId(driver.getId())
                .serviceArea(driver.getServiceArea())
                .currentLocation(toLocationResponse(driver.getCurrentLocation()))
                .build();
    }
}
