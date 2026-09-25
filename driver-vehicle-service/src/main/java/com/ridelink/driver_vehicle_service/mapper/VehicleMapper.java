package com.ridelink.driver_vehicle_service.mapper;

import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.dto.response.VehicleResponse;
import com.ridelink.driver_vehicle_service.model.Vehicle;

public class VehicleMapper {

    public static Vehicle toVehicle(String driverId, VehicleRegisterRequest request) {
        if (request == null) return null;
        return Vehicle.builder()
                .driverId(driverId)
                .registrationNumber(request.getRegistrationNumber())
                .make(request.getMake())
                .model(request.getModel())
                .vehicleType(request.getVehicleType())
                .color(request.getColor())
                .year(request.getYear())
                .build();
    }

    public static VehicleResponse toVehicleResponse(Vehicle vehicle) {
        if (vehicle == null) return null;
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .driverId(vehicle.getDriverId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .vehicleType(vehicle.getVehicleType())
                .color(vehicle.getColor())
                .year(vehicle.getYear())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}
