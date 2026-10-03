package com.transithub.mapper;

import com.transithub.dto.response.TransportationResponse;
import com.transithub.entity.Transportation;
import org.springframework.stereotype.Component;

@Component
public class TransportationMapper {

    public TransportationResponse toResponse(Transportation transportation) {
        // getTransportationType() and getTypeDetails() are answered by the real subclass
        return new TransportationResponse(
                transportation.getId(),
                transportation.getName(),
                transportation.getCode(),
                transportation.getTransportationType(),
                transportation.getDescription(),
                transportation.getTypeDetails());
    }
}
