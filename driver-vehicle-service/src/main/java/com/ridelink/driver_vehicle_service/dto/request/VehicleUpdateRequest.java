package com.ridelink.driver_vehicle_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleUpdateRequest {

    @NotBlank(message = "Make must not be blank")
    private String make;

    @NotBlank(message = "Model must not be blank")
    private String model;

    @NotBlank(message = "Vehicle type must not be blank")
    private String vehicleType;

    @NotBlank(message = "Color must not be blank")
    private String color;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be 1886 or later")
    private Integer year;
}
