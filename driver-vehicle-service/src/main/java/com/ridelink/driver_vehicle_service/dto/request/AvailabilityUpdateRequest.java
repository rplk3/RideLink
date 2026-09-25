package com.ridelink.driver_vehicle_service.dto.request;

import com.ridelink.driver_vehicle_service.enums.AvailabilityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityUpdateRequest {

    @NotNull(message = "Availability status is required")
    private AvailabilityStatus availabilityStatus;
}
