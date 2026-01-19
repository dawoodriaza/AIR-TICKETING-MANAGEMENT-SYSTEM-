package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.FlightRequest;
import com.ga.airticketmanagement.dto.request.UpdateFlightByOriginAirportRequest;
import com.ga.airticketmanagement.dto.response.FlightResponse;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/airports")
public class AirportFlightController {

    private FlightService flightService;

    @Autowired
    public void setFlightService(FlightService flightService) {
        this.flightService = flightService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{originAirportId}/flights")
    public FlightResponse createFlightByOriginAirport(@PathVariable Long originAirportId, @RequestBody FlightRequest flightObject) {

        return flightService.createFlightByOriginAirport(originAirportId, flightObject);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{airportId}/flights/{flightId}")
    public FlightResponse updateFlight(@PathVariable Long airportId, @PathVariable Long flightId, @RequestBody UpdateFlightByOriginAirportRequest flightObject) {

        return flightService.updateFlightByOriginAirport(airportId, flightId, flightObject);
    }

    @GetMapping("/{airportId}/flights/{flightId}")
    public FlightResponse getFlight(@PathVariable Long airportId, @PathVariable Long flightId) {

        return flightService.getFlightOfAirport(airportId, flightId);
    }

    @GetMapping("/{airportId}/flights")
    public ListResponse<FlightResponse> getAirportFlights(@PathVariable Long airportId, Pageable pageable) {

        return flightService.getAirportFlights(airportId, pageable);
    }

    @GetMapping("/{airportId}/departures")
    public ListResponse<FlightResponse> getAirportDepartures(@PathVariable Long airportId, Pageable pageable) {

        return flightService.getAirportDepartures(airportId, pageable);
    }

    @GetMapping("/{airportId}/arrivals")
    public ListResponse<FlightResponse> getAirportArrivals(@PathVariable Long airportId, Pageable pageable) {

        return flightService.getAirportArrivals(airportId, pageable);
    }
}
