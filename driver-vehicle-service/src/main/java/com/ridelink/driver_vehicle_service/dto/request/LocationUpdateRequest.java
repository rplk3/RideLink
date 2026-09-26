package com.ridelink.driver_vehicle_service.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standalone request DTO for updating a driver's current location.
 * Kept separate from {@link LocationRequest} so both DTOs can evolve
 * independently (e.g., location update may later include accuracy or
 * provider fields without affecting the nested creation object).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationUpdateRequest {

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0",  message = "Latitude must be >= -90.0")
    @DecimalMax(value = "90.0",   message = "Latitude must be <= 90.0")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180.0")
    @DecimalMax(value = "180.0",  message = "Longitude must be <= 180.0")
    private Double longitude;
}
