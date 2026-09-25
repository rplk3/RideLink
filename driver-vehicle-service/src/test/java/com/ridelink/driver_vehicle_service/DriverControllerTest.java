package com.ridelink.driver_vehicle_service;

import tools.jackson.databind.json.JsonMapper;
import com.ridelink.driver_vehicle_service.controller.DriverController;
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
import com.ridelink.driver_vehicle_service.service.DriverService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DriverController.class)
class DriverControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private DriverService driverService;

    @Test
    @DisplayName("1. POST valid driver -> 201")
    void createDriver_Valid_Returns201() throws Exception {
        DriverCreateRequest request = new DriverCreateRequest("acc-1", "lic-1", "AreaA", new LocationRequest(10.0, 20.0));
        DriverResponse response = DriverResponse.builder().id("drv-1").accountId("acc-1").build();
        when(driverService.createDriver(any(DriverCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("drv-1"))
                .andExpect(jsonPath("$.accountId").value("acc-1"));
    }

    @Test
    @DisplayName("2. POST invalid driver -> 400")
    void createDriver_Invalid_Returns400() throws Exception {
        DriverCreateRequest request = new DriverCreateRequest("", "lic-1", "AreaA", new LocationRequest(10.0, 20.0));
        // Empty accountId triggers validation

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.path").value("/api/drivers"))
                .andExpect(jsonPath("$.fieldErrors.accountId").exists());
    }

    @Test
    @DisplayName("3. POST duplicate account/license scenario -> 409")
    void createDriver_Duplicate_Returns409() throws Exception {
        DriverCreateRequest request = new DriverCreateRequest("acc-1", "lic-1", "AreaA", new LocationRequest(10.0, 20.0));
        when(driverService.createDriver(any(DriverCreateRequest.class)))
                .thenThrow(new DuplicateAccountIdException("Account ID already registered: acc-1"));

        mockMvc.perform(post("/api/drivers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Account ID already registered: acc-1"));
    }

    @Test
    @DisplayName("4. GET existing driver -> 200")
    void getDriver_Existing_Returns200() throws Exception {
        DriverResponse response = DriverResponse.builder().id("drv-1").accountId("acc-1").build();
        when(driverService.getDriverById("drv-1")).thenReturn(response);

        mockMvc.perform(get("/api/drivers/drv-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("drv-1"));
    }

    @Test
    @DisplayName("5. GET missing driver -> 404")
    void getDriver_Missing_Returns404() throws Exception {
        when(driverService.getDriverById("drv-1")).thenThrow(new DriverNotFoundException("Not found"));

        mockMvc.perform(get("/api/drivers/drv-1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("6. PUT valid driver -> 200")
    void updateDriverProfile_Valid_Returns200() throws Exception {
        DriverUpdateRequest request = new DriverUpdateRequest("lic-2", "AreaB");
        DriverResponse response = DriverResponse.builder().id("drv-1").licenseNumber("lic-2").build();
        when(driverService.updateDriverProfile(eq("drv-1"), any(DriverUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/drivers/drv-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licenseNumber").value("lic-2"));
    }

    @Test
    @DisplayName("7. PATCH availability -> 200")
    void updateDriverAvailability_Returns200() throws Exception {
        AvailabilityUpdateRequest request = new AvailabilityUpdateRequest(AvailabilityStatus.AVAILABLE);
        DriverResponse response = DriverResponse.builder().id("drv-1").availabilityStatus(AvailabilityStatus.AVAILABLE).build();
        when(driverService.updateDriverAvailability(eq("drv-1"), any(AvailabilityUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/drivers/drv-1/availability")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityStatus").value("AVAILABLE"));
    }

    @Test
    @DisplayName("8. PATCH location -> 200")
    void updateDriverLocation_Returns200() throws Exception {
        LocationUpdateRequest request = new LocationUpdateRequest(15.0, 25.0);
        DriverResponse response = DriverResponse.builder().id("drv-1").build();
        when(driverService.updateDriverLocation(eq("drv-1"), any(LocationUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(patch("/api/drivers/drv-1/location")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("9. GET eligible drivers -> 200")
    void getEligibleDrivers_Returns200() throws Exception {
        EligibleDriverResponse response = EligibleDriverResponse.builder().driverId("drv-1").serviceArea("AreaA").build();
        when(driverService.getEligibleDrivers("AreaA")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/drivers/eligible").param("serviceArea", "AreaA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].driverId").value("drv-1"));
    }

    @Test
    @DisplayName("10. eligible drivers with zero matches -> 200 and []")
    void getEligibleDrivers_Empty_Returns200() throws Exception {
        when(driverService.getEligibleDrivers("AreaA")).thenReturn(List.of());

        mockMvc.perform(get("/api/drivers/eligible").param("serviceArea", "AreaA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("11. missing serviceArea -> 400")
    void getEligibleDrivers_MissingParam_Returns400() throws Exception {
        mockMvc.perform(get("/api/drivers/eligible"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("12. blank serviceArea -> 400")
    void getEligibleDrivers_BlankParam_Returns400() throws Exception {
        mockMvc.perform(get("/api/drivers/eligible").param("serviceArea", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.serviceArea").exists());
    }
}
