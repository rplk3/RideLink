package com.ridelink.driver_vehicle_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Minimal response DTO for the Ride Management Service.
 * Exposes only the fields needed to assess driver eligibility:
 * identity, coverage area, and current position.
 * Internal fields (accountId, licenseNumber, timestamps, etc.)
 * are deliberately excluded.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibleDriverResponse {

    private String driverId;
    private String serviceArea;
    private LocationResponse currentLocation;
}
