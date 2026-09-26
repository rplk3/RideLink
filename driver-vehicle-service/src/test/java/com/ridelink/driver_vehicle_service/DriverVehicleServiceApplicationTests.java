package com.ridelink.driver_vehicle_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifies that the Spring application context initializes without errors.
 * The spring.mongodb.uri property is overridden here with a test-only value so
 * that this test does not require MONGODB_URI or any Atlas credentials to be
 * present in the test environment.
 *
 * <p>This test does NOT verify MongoDB persistence correctness.
 */
@SpringBootTest(properties = {
		"spring.mongodb.uri=mongodb://localhost:27017/driver_test"
})
class DriverVehicleServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
