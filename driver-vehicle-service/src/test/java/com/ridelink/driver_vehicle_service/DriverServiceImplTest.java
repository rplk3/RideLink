package com.ridelink.driver_vehicle_service;

import com.ridelink.driver_vehicle_service.dto.request.AvailabilityUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverCreateRequest;
import com.ridelink.driver_vehicle_service.dto.request.DriverUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationRequest;
import com.ridelink.driver_vehicle_service.dto.request.LocationUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.DriverResponse;
import com.ridelink.driver_vehicle_service.dto.response.EligibleDriverResponse;
import com.ridelink.driver_vehicle_service.enums.AvailabilityStatus;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.exception.DuplicateAccountIdException;
import com.ridelink.driver_vehicle_service.exception.DuplicateLicenseNumberException;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.Location;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.service.DriverServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceImplTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverServiceImpl driverService;

    @Test
    @DisplayName("Create driver - success defaults to OFFLINE")
    void createDriver_Success() {
        DriverCreateRequest request = new DriverCreateRequest("acc-1", "lic-1", "AreaA", new LocationRequest(10.0, 20.0));
        when(driverRepository.existsByAccountId("acc-1")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("lic-1")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenAnswer(i -> {
            Driver d = i.getArgument(0);
            d.setId("drv-1");
            return d;
        });

        DriverResponse response = driverService.createDriver(request);

        assertThat(response.getId()).isEqualTo("drv-1");
        assertThat(response.getAccountId()).isEqualTo("acc-1");
        assertThat(response.getAvailabilityStatus()).isEqualTo(AvailabilityStatus.OFFLINE);
        verify(driverRepository).save(any(Driver.class));
    }

    @Test
    @DisplayName("Create driver - duplicate accountId rejected")
    void createDriver_DuplicateAccountId() {
        DriverCreateRequest request = new DriverCreateRequest("acc-1", "lic-1", "AreaA", new LocationRequest(10.0, 20.0));
        when(driverRepository.existsByAccountId("acc-1")).thenReturn(true);

        assertThrows(DuplicateAccountIdException.class, () -> driverService.createDriver(request));
        verify(driverRepository, never()).save(any());
    }

    @Test
    @DisplayName("Create driver - duplicate licenseNumber rejected")
    void createDriver_DuplicateLicenseNumber() {
        DriverCreateRequest request = new DriverCreateRequest("acc-1", "lic-1", "AreaA", new LocationRequest(10.0, 20.0));
        when(driverRepository.existsByAccountId("acc-1")).thenReturn(false);
        when(driverRepository.existsByLicenseNumber("lic-1")).thenReturn(true);

        assertThrows(DuplicateLicenseNumberException.class, () -> driverService.createDriver(request));
        verify(driverRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get driver by ID - success")
    void getDriverById_Success() {
        Driver driver = Driver.builder().id("drv-1").accountId("acc-1").build();
        when(driverRepository.findById("drv-1")).thenReturn(Optional.of(driver));

        DriverResponse response = driverService.getDriverById("drv-1");

        assertThat(response.getAccountId()).isEqualTo("acc-1");
    }

    @Test
    @DisplayName("Get driver by ID - not found")
    void getDriverById_NotFound() {
        when(driverRepository.findById("drv-1")).thenReturn(Optional.empty());

        assertThrows(DriverNotFoundException.class, () -> driverService.getDriverById("drv-1"));
    }

    @Test
    @DisplayName("Update driver profile - success")
    void updateDriverProfile_Success() {
        Driver driver = Driver.builder().id("drv-1").licenseNumber("lic-old").serviceArea("AreaOld").build();
        when(driverRepository.findById("drv-1")).thenReturn(Optional.of(driver));
        
        // Changing to a new license number that isn't duplicate
        when(driverRepository.existsByLicenseNumber("lic-new")).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        DriverUpdateRequest request = new DriverUpdateRequest("lic-new", "AreaNew");
        DriverResponse response = driverService.updateDriverProfile("drv-1", request);

        assertThat(response.getLicenseNumber()).isEqualTo("lic-new");
        assertThat(response.getServiceArea()).isEqualTo("AreaNew");
        verify(driverRepository).save(driver);
    }

    @Test
    @DisplayName("Update driver profile - duplicate license rejected")
    void updateDriverProfile_DuplicateLicense() {
        Driver driver = Driver.builder().id("drv-1").licenseNumber("lic-old").build();
        when(driverRepository.findById("drv-1")).thenReturn(Optional.of(driver));
        when(driverRepository.existsByLicenseNumber("lic-new")).thenReturn(true);

        DriverUpdateRequest request = new DriverUpdateRequest("lic-new", "AreaNew");
        assertThrows(DuplicateLicenseNumberException.class, () -> driverService.updateDriverProfile("drv-1", request));
        verify(driverRepository, never()).save(any());
    }

    @Test
    @DisplayName("Update driver availability - success")
    void updateDriverAvailability_Success() {
        Driver driver = Driver.builder().id("drv-1").availabilityStatus(AvailabilityStatus.OFFLINE).build();
        when(driverRepository.findById("drv-1")).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest(AvailabilityStatus.AVAILABLE);
        DriverResponse response = driverService.updateDriverAvailability("drv-1", request);

        assertThat(response.getAvailabilityStatus()).isEqualTo(AvailabilityStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Update driver availability - not found")
    void updateDriverAvailability_NotFound() {
        when(driverRepository.findById("drv-1")).thenReturn(Optional.empty());
        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest(AvailabilityStatus.AVAILABLE);

        assertThrows(DriverNotFoundException.class, () -> driverService.updateDriverAvailability("drv-1", request));
    }

    @Test
    @DisplayName("Update driver location - success")
    void updateDriverLocation_Success() {
        Driver driver = Driver.builder().id("drv-1").currentLocation(new Location(0, 0)).build();
        when(driverRepository.findById("drv-1")).thenReturn(Optional.of(driver));
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        LocationUpdateRequest request = new LocationUpdateRequest(10.0, 20.0);
        DriverResponse response = driverService.updateDriverLocation("drv-1", request);

        assertThat(response.getCurrentLocation().getLatitude()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Update driver location - not found")
    void updateDriverLocation_NotFound() {
        when(driverRepository.findById("drv-1")).thenReturn(Optional.empty());
        LocationUpdateRequest request = new LocationUpdateRequest(10.0, 20.0);

        assertThrows(DriverNotFoundException.class, () -> driverService.updateDriverLocation("drv-1", request));
    }

    @Test
    @DisplayName("Get eligible drivers - mapping hides fields")
    void getEligibleDrivers_FiltersFields() {
        Driver driver = Driver.builder()
                .id("drv-1")
                .accountId("acc-secret")
                .licenseNumber("lic-secret")
                .serviceArea("AreaA")
                .currentLocation(new Location(10.0, 20.0))
                .build();

        when(driverRepository.findByAvailabilityStatusAndServiceAreaIgnoreCase(AvailabilityStatus.AVAILABLE, "AreaA"))
                .thenReturn(List.of(driver));

        List<EligibleDriverResponse> responses = driverService.getEligibleDrivers("AreaA");

        assertThat(responses).hasSize(1);
        EligibleDriverResponse resp = responses.get(0);
        assertThat(resp.getDriverId()).isEqualTo("drv-1");
        assertThat(resp.getServiceArea()).isEqualTo("AreaA");
        assertThat(resp.getCurrentLocation().getLatitude()).isEqualTo(10.0);
        
        // Assert we don't have access to secret fields (they don't even exist in the DTO, 
        // but we verify by asserting it's correctly mapped)
    }
}
