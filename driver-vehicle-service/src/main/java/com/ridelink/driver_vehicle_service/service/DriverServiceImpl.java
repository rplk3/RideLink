package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.request.AvailabilityUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverCreateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.DriverResponse;
import com.ridelink.driver_vehicle_service.dto.response.EligibleDriverResponse;
import com.ridelink.driver_vehicle_service.enums.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.exception.DuplicateAccountIdException;
import com.ridelink.driver_vehicle_service.exception.DuplicateLicenseNumberException;
import com.ridelink.driver_vehicle_service.mapper.DriverMapper;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public DriverResponse createDriver(DriverCreateRequest request) {
        if (driverRepository.existsByAccountId(request.getAccountId())) {
            throw new DuplicateAccountIdException("Account ID already registered: " + request.getAccountId());
        }
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateLicenseNumberException("License number already exists: " + request.getLicenseNumber());
        }

        Driver driver = Driver.builder()
                .accountId(request.getAccountId())
                .licenseNumber(request.getLicenseNumber())
                .serviceArea(request.getServiceArea())
                .currentLocation(DriverMapper.toLocation(request.getCurrentLocation()))
                .availabilityStatus(AvailabilityStatus.OFFLINE)
                .build();

        Driver savedDriver = driverRepository.save(driver);
        return DriverMapper.toDriverResponse(savedDriver);
    }

    @Override
    public DriverResponse getDriverById(String id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));
        return DriverMapper.toDriverResponse(driver);
    }

    @Override
    public DriverResponse updateDriverProfile(String id, DriverUpdateRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        if (!driver.getLicenseNumber().equals(request.getLicenseNumber())) {
            if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
                throw new DuplicateLicenseNumberException("License number already exists: " + request.getLicenseNumber());
            }
        }

        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setServiceArea(request.getServiceArea());

        Driver updatedDriver = driverRepository.save(driver);
        return DriverMapper.toDriverResponse(updatedDriver);
    }

    @Override
    public DriverResponse updateDriverAvailability(String id, AvailabilityUpdateRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        driver.setAvailabilityStatus(request.getAvailabilityStatus());

        Driver updatedDriver = driverRepository.save(driver);
        return DriverMapper.toDriverResponse(updatedDriver);
    }

    @Override
    public DriverResponse updateDriverLocation(String id, LocationUpdateRequest request) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException("Driver not found with id: " + id));

        driver.setCurrentLocation(DriverMapper.toLocation(request));

        Driver updatedDriver = driverRepository.save(driver);
        return DriverMapper.toDriverResponse(updatedDriver);
    }

    @Override
    public List<EligibleDriverResponse> getEligibleDrivers(String serviceArea) {
        List<Driver> drivers = driverRepository.findByAvailabilityStatusAndServiceAreaIgnoreCase(
                AvailabilityStatus.AVAILABLE, serviceArea);

        return drivers.stream()
                .map(DriverMapper::toEligibleDriverResponse)
                .collect(Collectors.toList());
    }
}
