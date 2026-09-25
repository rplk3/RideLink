package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.dto.request.VehicleUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.VehicleResponse;
import com.ridelink.driver_vehicle_service.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/drivers/{driverId}/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleResponse registerVehicle(
            @PathVariable String driverId,
            @Valid @RequestBody VehicleRegisterRequest request) {
        return vehicleService.registerVehicle(driverId, request);
    }

    @GetMapping("/drivers/{driverId}/vehicles")
    public List<VehicleResponse> getVehiclesForDriver(@PathVariable String driverId) {
        return vehicleService.getVehiclesForDriver(driverId);
    }

    @GetMapping("/vehicles/{vehicleId}")
    public VehicleResponse getVehicleById(@PathVariable String vehicleId) {
        return vehicleService.getVehicleById(vehicleId);
    }

    @PutMapping("/vehicles/{vehicleId}")
    public VehicleResponse updateVehicle(
            @PathVariable String vehicleId,
            @Valid @RequestBody VehicleUpdateRequest request) {
        return vehicleService.updateVehicle(vehicleId, request);
    }
}
