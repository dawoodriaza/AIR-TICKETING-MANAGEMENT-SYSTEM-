package com.ga.airticketmanagement.dto.mapper;

import com.ga.airticketmanagement.dto.request.CreateFlightByOriginAirportRequest;
import com.ga.airticketmanagement.dto.request.FlightRequest;
import com.ga.airticketmanagement.dto.response.FlightResponse;
import com.ga.airticketmanagement.model.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public FlightResponse toResponse(Flight flight) {
        return new FlightResponse(
                flight.getId(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.getCreatedAt(),
                flight.getUpdatedAt(),
                flight.getOriginAirport().getId(),
                flight.getDestinationAirport().getId(),
                flight.getUser().getId()
        );
    }

    public Flight toEntity(FlightRequest request) {
        Flight flight = new Flight();
        flight.setDepartureTime(request.getDepartureTime());
        flight.setArrivalTime(request.getArrivalTime());
        flight.setPrice(request.getPrice());
        return flight;
    }

}
