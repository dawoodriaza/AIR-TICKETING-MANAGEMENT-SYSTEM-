package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.AirportRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.AirportResponse;
import com.ga.airticketmanagement.service.AirportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api")
public class AirportController {

    private AirportService airportService;

    @Autowired
    public void setAirportService(AirportService airportService){
        this.airportService = airportService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/airports")
    public AirportResponse createAirport(@Valid @RequestBody AirportRequest airport){

        return airportService.createAirport(airport);
    }

    @GetMapping("/airports/{id}")
    public AirportResponse getAirport(@PathVariable Long id){

        return airportService.getAirport(id);
    }

    @GetMapping("/airports")
    public ListResponse<AirportResponse> getAirports(Pageable pageable){

        return airportService.getAirports(pageable);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/airports/{id}")
    public AirportResponse updateAirport(@PathVariable Long id,@Valid @RequestBody AirportRequest airport){

        return airportService.updateAirport(id, airport);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/airports/{id}")
    public void deleteAirport(@PathVariable Long id){
        airportService.deleteAirport(id);
    }
}
