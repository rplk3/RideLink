package com.ridelink.driver_vehicle_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {

    private String id;
    private String driverId;
    private String registrationNumber;
    private String make;
    private String model;
    private String vehicleType;
    private String color;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
