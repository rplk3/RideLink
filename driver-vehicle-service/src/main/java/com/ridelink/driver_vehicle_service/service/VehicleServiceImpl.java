package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.dto.request.VehicleUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.VehicleResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.exception.DuplicateRegistrationNumberException;
import com.ridelink.driver_vehicle_service.exception.VehicleNotFoundException;
import com.ridelink.driver_vehicle_service.mapper.VehicleMapper;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, DriverRepository driverRepository) {
        this.vehicleRepository = vehicleRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public VehicleResponse registerVehicle(String driverId, VehicleRegisterRequest request) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver not found with id: " + driverId);
        }

        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateRegistrationNumberException("Registration number already exists: " + request.getRegistrationNumber());
        }

        Vehicle vehicle = VehicleMapper.toVehicle(driverId, request);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return VehicleMapper.toVehicleResponse(savedVehicle);
    }

    @Override
    public List<VehicleResponse> getVehiclesForDriver(String driverId) {
        if (!driverRepository.existsById(driverId)) {
            throw new DriverNotFoundException("Driver not found with id: " + driverId);
        }

        List<Vehicle> vehicles = vehicleRepository.findByDriverId(driverId);
        return vehicles.stream()
                .map(VehicleMapper::toVehicleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VehicleResponse getVehicleById(String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        return VehicleMapper.toVehicleResponse(vehicle);
    }

    @Override
    public VehicleResponse updateVehicle(String id, VehicleUpdateRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setColor(request.getColor());
        vehicle.setYear(request.getYear());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return VehicleMapper.toVehicleResponse(updatedVehicle);
    }
}
