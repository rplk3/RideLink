package com.ridelink.driver_vehicle_service;

import tools.jackson.databind.json.JsonMapper;
import com.ridelink.driver_vehicle_service.controller.VehicleController;
import com.ridelink.driver_vehicle_service.dto.request.VehicleRegisterRequest;
import com.ridelink.driver_vehicle_service.dto.request.VehicleUpdateRequest;
import com.ridelink.driver_vehicle_service.dto.response.VehicleResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.exception.DuplicateRegistrationNumberException;
import com.ridelink.driver_vehicle_service.exception.VehicleNotFoundException;
import com.ridelink.driver_vehicle_service.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private VehicleService vehicleService;

    @Test
    @DisplayName("1. POST valid registration -> 201")
    void registerVehicle_Valid_Returns201() throws Exception {
        VehicleRegisterRequest request = new VehicleRegisterRequest("reg-1", "Make", "Model", "SUV", "Red", 2022);
        VehicleResponse response = VehicleResponse.builder().id("veh-1").registrationNumber("reg-1").build();
        when(vehicleService.registerVehicle(eq("drv-1"), any(VehicleRegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/drivers/drv-1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("veh-1"));
    }

    @Test
    @DisplayName("2. POST invalid body -> 400")
    void registerVehicle_Invalid_Returns400() throws Exception {
        VehicleRegisterRequest request = new VehicleRegisterRequest("", "Make", "Model", "SUV", "Red", 2022);
        // blank reg number

        mockMvc.perform(post("/api/drivers/drv-1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.registrationNumber").exists());
    }

    @Test
    @DisplayName("3. duplicate registration -> 409")
    void registerVehicle_Duplicate_Returns409() throws Exception {
        VehicleRegisterRequest request = new VehicleRegisterRequest("reg-1", "Make", "Model", "SUV", "Red", 2022);
        when(vehicleService.registerVehicle(eq("drv-1"), any(VehicleRegisterRequest.class)))
                .thenThrow(new DuplicateRegistrationNumberException("Registration exists"));

        mockMvc.perform(post("/api/drivers/drv-1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("4. registration for missing driver -> 404")
    void registerVehicle_MissingDriver_Returns404() throws Exception {
        VehicleRegisterRequest request = new VehicleRegisterRequest("reg-1", "Make", "Model", "SUV", "Red", 2022);
        when(vehicleService.registerVehicle(eq("drv-1"), any(VehicleRegisterRequest.class)))
                .thenThrow(new DriverNotFoundException("Not found"));

        mockMvc.perform(post("/api/drivers/drv-1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("5. GET driver's vehicles -> 200")
    void getVehiclesForDriver_Returns200() throws Exception {
        VehicleResponse response = VehicleResponse.builder().id("veh-1").build();
        when(vehicleService.getVehiclesForDriver("drv-1")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/drivers/drv-1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("6. GET vehicle by ID -> 200")
    void getVehicleById_Returns200() throws Exception {
        VehicleResponse response = VehicleResponse.builder().id("veh-1").build();
        when(vehicleService.getVehicleById("veh-1")).thenReturn(response);

        mockMvc.perform(get("/api/vehicles/veh-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("veh-1"));
    }

    @Test
    @DisplayName("7. GET missing vehicle -> 404")
    void getVehicleById_Missing_Returns404() throws Exception {
        when(vehicleService.getVehicleById("veh-1")).thenThrow(new VehicleNotFoundException("Not found"));

        mockMvc.perform(get("/api/vehicles/veh-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("8. PUT valid vehicle -> 200")
    void updateVehicle_Valid_Returns200() throws Exception {
        VehicleUpdateRequest request = new VehicleUpdateRequest("Make", "Model", "SUV", "Blue", 2023);
        VehicleResponse response = VehicleResponse.builder().id("veh-1").color("Blue").build();
        when(vehicleService.updateVehicle(eq("veh-1"), any(VehicleUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/vehicles/veh-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Blue"));
    }
}
