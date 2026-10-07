package com.transithub.dto;

import com.transithub.dto.request.FareRequest;
import com.transithub.dto.request.RouteRequest;
import com.transithub.dto.request.StopRequest;
import com.transithub.entity.enums.RouteStatus;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks the Bean Validation rules on the request DTOs (no Spring, no database).
 * These rules run in the controller, before any service is called.
 */
class DtoValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validStopHasNoViolations() {
        StopRequest stop = new StopRequest("Test Stop", null, 13.94, 121.16);
        assertTrue(validator.validate(stop).isEmpty(), "a valid stop should have no violations");
    }

    @Test
    void stopWithBlankNameAndBadCoordinatesIsRejected() {
        StopRequest stop = new StopRequest("  ", null, 95.0, 200.0);
        // blank name + latitude too high + longitude too high
        assertEquals(3, validator.validate(stop).size());
    }

    @Test
    void coordinateOutOfRangeIsRejected() {
        assertFalse(validator.validate(new CoordinateDto(-91.0, 0.0)).isEmpty(), "latitude -91 is invalid");
        assertTrue(validator.validate(new CoordinateDto(-90.0, 180.0)).isEmpty(), "the limits themselves are valid");
    }

    @Test
    void negativeFareIsRejected() {
        FareRequest fare = new FareRequest(new BigDecimal("-1.00"), new BigDecimal("1.00"));
        assertFalse(validator.validate(fare).isEmpty(), "a negative fare is invalid");
    }

    @Test
    void routeWithoutStopsOrFareIsRejected() {
        RouteRequest route = new RouteRequest("R-1", "Name", "A", "B", RouteStatus.ACTIVE,
                30, 10.0, 1L, null, List.of(), null, null);
        // missing fare + empty stop list
        assertEquals(2, validator.validate(route).size());
    }
}
