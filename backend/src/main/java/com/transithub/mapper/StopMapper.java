package com.transithub.mapper;

import com.transithub.dto.response.StopResponse;
import com.transithub.entity.Stop;
import org.springframework.stereotype.Component;

/** Converts a Stop entity into the data we send to the frontend. */
@Component
public class StopMapper {

    public StopResponse toResponse(Stop stop) {
        return new StopResponse(
                stop.getId(),
                stop.getName(),
                stop.getDescription(),
                stop.getLatitude(),
                stop.getLongitude(),
                stop.isDemoData());
    }
}
