package com.ridelink.driver_vehicle_service.repository;

import com.ridelink.driver_vehicle_service.enums.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.model.Driver;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends MongoRepository<Driver, String> {

    Optional<Driver> findByAccountId(String accountId);

    boolean existsByAccountId(String accountId);

    Optional<Driver> findByLicenseNumber(String licenseNumber);

    boolean existsByLicenseNumber(String licenseNumber);

    List<Driver> findByAvailabilityStatusAndServiceAreaIgnoreCase(AvailabilityStatus availabilityStatus, String serviceArea);
}
