package com.transithub.dto.request;

import com.transithub.entity.enums.TransportType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Create or update a transportation service.
 * The fields after "description" are only used by the matching type
 * (airConditioned for BUS, modernized for JEEPNEY, seatingCapacity for VAN,
 * serviceArea for SHUTTLE, numberOfCars for TRAIN).
 */
public record TransportationRequest(
        @NotNull TransportType type,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 30) String code,
        @Size(max = 500) String description,
        Boolean airConditioned,
        Boolean modernized,
        Integer seatingCapacity,
        @Size(max = 100) String serviceArea,
        Integer numberOfCars) {
}
