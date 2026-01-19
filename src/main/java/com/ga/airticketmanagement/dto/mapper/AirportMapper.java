package com.ga.airticketmanagement.dto.mapper;

import com.ga.airticketmanagement.dto.request.AirportRequest;
import com.ga.airticketmanagement.dto.response.AirportResponse;
import com.ga.airticketmanagement.model.Airport;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    AirportResponse toResponse(Airport airport);
    Airport toEntity(AirportRequest airportRequest);
}
