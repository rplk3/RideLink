package com.ridelink.driver_vehicle_service.dto.response;

import com.ridelink.driver_vehicle_service.enums.AvailabilityStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {

    private String id;
    private String accountId;
    private String licenseNumber;
    private AvailabilityStatus availabilityStatus;
    private String serviceArea;
    private LocationResponse currentLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
