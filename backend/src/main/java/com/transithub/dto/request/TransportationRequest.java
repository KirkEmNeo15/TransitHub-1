package com.transithub.dto.request;

import com.transithub.entity.enums.TransportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Create or update a transportation service.
 * The fields after "description" are only used by the matching type
 * (airConditioned for BUS, modernized for JEEPNEY, seatingCapacity for VAN).
 */
public record TransportationRequest(
        @NotNull(message = "Type is required (BUS, JEEPNEY or VAN)")
        TransportType type,

        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name must be at most 100 characters")
        String name,

        @NotBlank(message = "Code cannot be empty")
        @Size(max = 30, message = "Code must be at most 30 characters")
        String code,

        @Size(max = 500, message = "Description must be at most 500 characters")
        String description,

        Boolean airConditioned,
        Boolean modernized,
        Integer seatingCapacity) {
}
