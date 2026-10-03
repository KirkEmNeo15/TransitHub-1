package com.transithub.dto.response;

import java.math.BigDecimal;

public record FareResponse(BigDecimal baseFare, BigDecimal perKmRate) {
}
