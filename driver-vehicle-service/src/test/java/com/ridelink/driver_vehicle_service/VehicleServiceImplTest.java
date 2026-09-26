package com.ridelink.driver_vehicle_service;

import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.dto.request.VehicleUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.VehicleResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.exception.DuplicateRegistrationNumberException;
import com.ridelink.driver_vehicle_service.exception.VehicleNotFoundException;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import com.ridelink.driver_vehicle_service.repository.VehicleRepository;
import com.ridelink.driver_vehicle_service.service.VehicleServiceImpl;
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
class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleService;

    @Test
    @DisplayName("Register vehicle - success")
    void registerVehicle_Success() {
        VehicleRegisterRequest request = new VehicleRegisterRequest("reg-1", "Make", "Model", "SUV", "Red", 2022);
        when(driverRepository.existsById("drv-1")).thenReturn(true);
        when(vehicleRepository.existsByRegistrationNumber("reg-1")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> {
            Vehicle v = i.getArgument(0);
            v.setId("veh-1");
            return v;
        });

        VehicleResponse response = vehicleService.registerVehicle("drv-1", request);

        assertThat(response.getId()).isEqualTo("veh-1");
        assertThat(response.getDriverId()).isEqualTo("drv-1");
        assertThat(response.getRegistrationNumber()).isEqualTo("reg-1");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Register vehicle - missing driver rejected")
    void registerVehicle_MissingDriver() {
        VehicleRegisterRequest request = new VehicleRegisterRequest("reg-1", "Make", "Model", "SUV", "Red", 2022);
        when(driverRepository.existsById("drv-1")).thenReturn(false);

        assertThrows(DriverNotFoundException.class, () -> vehicleService.registerVehicle("drv-1", request));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Register vehicle - duplicate registration rejected")
    void registerVehicle_DuplicateRegistration() {
        VehicleRegisterRequest request = new VehicleRegisterRequest("reg-1", "Make", "Model", "SUV", "Red", 2022);
        when(driverRepository.existsById("drv-1")).thenReturn(true);
        when(vehicleRepository.existsByRegistrationNumber("reg-1")).thenReturn(true);

        assertThrows(DuplicateRegistrationNumberException.class, () -> vehicleService.registerVehicle("drv-1", request));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get vehicles for driver - success")
    void getVehiclesForDriver_Success() {
        when(driverRepository.existsById("drv-1")).thenReturn(true);
        Vehicle v = Vehicle.builder().id("veh-1").driverId("drv-1").build();
        when(vehicleRepository.findByDriverId("drv-1")).thenReturn(List.of(v));

        List<VehicleResponse> list = vehicleService.getVehiclesForDriver("drv-1");

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo("veh-1");
    }

    @Test
    @DisplayName("Get vehicles for driver - missing driver rejected")
    void getVehiclesForDriver_MissingDriver() {
        when(driverRepository.existsById("drv-1")).thenReturn(false);

        assertThrows(DriverNotFoundException.class, () -> vehicleService.getVehiclesForDriver("drv-1"));
        verify(vehicleRepository, never()).findByDriverId(any());
    }

    @Test
    @DisplayName("Get vehicle by ID - success")
    void getVehicleById_Success() {
        Vehicle v = Vehicle.builder().id("veh-1").build();
        when(vehicleRepository.findById("veh-1")).thenReturn(Optional.of(v));

        VehicleResponse response = vehicleService.getVehicleById("veh-1");
        
        assertThat(response.getId()).isEqualTo("veh-1");
    }

    @Test
    @DisplayName("Get vehicle by ID - not found")
    void getVehicleById_NotFound() {
        when(vehicleRepository.findById("veh-1")).thenReturn(Optional.empty());

        assertThrows(VehicleNotFoundException.class, () -> vehicleService.getVehicleById("veh-1"));
    }

    @Test
    @DisplayName("Update vehicle - success preserves unchangeable fields")
    void updateVehicle_Success() {
        Vehicle v = Vehicle.builder().id("veh-1").driverId("drv-1").registrationNumber("reg-1")
                .make("Old").model("Old").vehicleType("Old").color("Old").year(2000).build();
        
        when(vehicleRepository.findById("veh-1")).thenReturn(Optional.of(v));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(v);

        VehicleUpdateRequest request = new VehicleUpdateRequest("New", "New", "New", "New", 2025);
        VehicleResponse response = vehicleService.updateVehicle("veh-1", request);

        assertThat(response.getMake()).isEqualTo("New");
        assertThat(response.getRegistrationNumber()).isEqualTo("reg-1"); // Preserved
        assertThat(response.getDriverId()).isEqualTo("drv-1"); // Preserved
        verify(vehicleRepository).save(v);
    }

    @Test
    @DisplayName("Update vehicle - not found rejected")
    void updateVehicle_NotFound() {
        when(vehicleRepository.findById("veh-1")).thenReturn(Optional.empty());

        VehicleUpdateRequest request = new VehicleUpdateRequest("New", "New", "New", "New", 2025);
        assertThrows(VehicleNotFoundException.class, () -> vehicleService.updateVehicle("veh-1", request));
    }
}
