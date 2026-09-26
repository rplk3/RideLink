package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.dto.request.VehicleUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.VehicleResponse;

import java.util.List;

public interface VehicleService {

    VehicleResponse registerVehicle(String driverId, VehicleRegisterRequest request);

    List<VehicleResponse> getVehiclesForDriver(String driverId);

    VehicleResponse getVehicleById(String id);

    VehicleResponse updateVehicle(String id, VehicleUpdateRequest request);
}
