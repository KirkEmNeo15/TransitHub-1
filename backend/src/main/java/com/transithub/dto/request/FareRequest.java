package com.transithub.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FareRequest(
        @NotNull(message = "Base fare is required")
        @DecimalMin(value = "0.00", message = "Base fare cannot be negative")
        @Digits(integer = 6, fraction = 2, message = "Base fare can have at most 6 digits and 2 decimals")
        BigDecimal baseFare,

        @NotNull(message = "Rate per kilometer is required")
        @DecimalMin(value = "0.00", message = "Rate per kilometer cannot be negative")
        @Digits(integer = 6, fraction = 2, message = "Rate per kilometer can have at most 6 digits and 2 decimals")
        BigDecimal perKmRate) {
}
