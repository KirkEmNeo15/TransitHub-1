package com.transithub.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FareRequest(
        @NotNull @DecimalMin("0.00") @Digits(integer = 6, fraction = 2) BigDecimal baseFare,
        @NotNull @DecimalMin("0.00") @Digits(integer = 6, fraction = 2) BigDecimal perKmRate) {
}
