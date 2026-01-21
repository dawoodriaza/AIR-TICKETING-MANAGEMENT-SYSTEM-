package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.FlightRequest;
import com.ga.airticketmanagement.dto.response.FlightResponse;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(path = "/api")
public class FlightController {

    private FlightService flightService;

    @Autowired
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public ListResponse<FlightResponse> getFlights(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String flightNo,
            @RequestParam(required = false) Long originAirportId,
            @RequestParam(required = false) String originAirportName,
            @RequestParam(required = false) Long destinationAirportId,
            @RequestParam(required = false) String destinationAirportName,
            @RequestParam(required = false) java.math.BigDecimal price,
            @RequestParam(required = false) String search,
            Pageable pageable) {

        if (id != null || (flightNo != null && !flightNo.trim().isEmpty()) ||
            originAirportId != null || (originAirportName != null && !originAirportName.trim().isEmpty()) ||
            destinationAirportId != null || (destinationAirportName != null && !destinationAirportName.trim().isEmpty()) ||
            price != null || (search != null && !search.trim().isEmpty())) {
            return flightService.searchFlights(id, flightNo, originAirportId, originAirportName,
                    destinationAirportId, destinationAirportName, price, search, pageable);
        }

        return flightService.getFlights(pageable);
    }

    @GetMapping("/flights/{flightId}")
    public FlightResponse getFlight(@PathVariable Long flightId) {

        return flightService.getFlight(flightId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/flights")
    public FlightResponse createFlight(@RequestBody FlightRequest flightObject) {

        return flightService.createFlight(flightObject);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/flights/{flightId}")
    public FlightResponse updateFlight(@PathVariable Long flightId, @RequestBody FlightRequest flightObject) {

        return flightService.updateFlight(flightId, flightObject);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/flights/{flightId}")
    public void deleteFlight(@PathVariable Long flightId) {

        flightService.deleteFlight(flightId);
    }

    @GetMapping("/flights/browse")
    public ListResponse<FlightResponse> browseFlights(
            @RequestParam(required = false) Long originAirportId,
            @RequestParam(required = false) Long destinationAirportId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureTimeTo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalTimeTo,
            @RequestParam(required = false) java.math.BigDecimal minPrice,
            @RequestParam(required = false) java.math.BigDecimal maxPrice,
            Pageable pageable) {

        return flightService.browseFlights(
                originAirportId,
                destinationAirportId,
                departureTimeFrom,
                departureTimeTo,
                arrivalTimeFrom,
                arrivalTimeTo,
                minPrice,
                maxPrice,
                pageable
        );
    }


}
