package com.ridelink.driver_vehicle_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating editable driver profile fields.
 * All supplied String values are validated to be non-blank.
 * Fields are optional at the HTTP level — the service layer decides
 * which ones to apply — but if sent, they must not be blank.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequest {

    @NotBlank(message = "License number must not be blank")
    private String licenseNumber;

    @NotBlank(message = "Service area must not be blank")
    private String serviceArea;
}
