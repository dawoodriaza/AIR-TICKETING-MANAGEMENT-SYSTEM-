package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.FlightMapper;
import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.request.FlightByOriginAirportRequest;
import com.ga.airticketmanagement.dto.request.FlightRequest;
import com.ga.airticketmanagement.dto.response.FlightResponse;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.exception.ConflictedOriginDestinationException;
import com.ga.airticketmanagement.exception.FlightDeletionException;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Airport;
import com.ga.airticketmanagement.model.Flight;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.AirportRepository;
import com.ga.airticketmanagement.repository.FlightRepository;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import com.ga.airticketmanagement.specification.FlightSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final AirportRepository airportRepository;
    private final FlightMapper flightMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    public FlightService(FlightRepository flightRepository, AirportRepository airportRepository
            , FlightMapper flightMapper, AuthenticatedUserProvider authenticatedUserProvider) {
        this.flightRepository = flightRepository;
        this.airportRepository = airportRepository;
        this.flightMapper = flightMapper;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public FlightResponse createFlightByOriginAirport(Long originAirportId, FlightRequest flightObject) {

        Airport origin = airportRepository.findById(originAirportId).orElseThrow(
                () -> new InformationNotFoundException("Airport not found"));
        Airport destination = airportRepository.findById(flightObject.getDestinationAirportId()).orElseThrow(
                () -> new InformationNotFoundException("Destination Airport not found"));
        User user = authenticatedUserProvider.getAuthenticatedUser();

        if(origin.getId().equals(destination.getId())){
            throw new ConflictedOriginDestinationException("Destination Airport and Destination Airport are the same");
        }

        Flight flight = flightMapper.toEntity(flightObject);
        flight.setOriginAirport(origin);
        flight.setDestinationAirport(destination);
        flight.setPrice(flightObject.getPrice());
        flight.setDepartureTime(flightObject.getDepartureTime());
        flight.setArrivalTime(flightObject.getArrivalTime());
        flight.setUser(user);

        flight = flightRepository.save(flight);
        return flightMapper.toResponse(flight);
    }

    public FlightResponse createFlight(FlightRequest flightObject) {

        Airport origin = airportRepository.findById(flightObject.getOriginAirportId()).orElseThrow(
                () -> new InformationNotFoundException("Airport not found"));
        Airport destination = airportRepository.findById(flightObject.getDestinationAirportId()).orElseThrow(
                () -> new InformationNotFoundException("Destination Airport not found"));
        User user = authenticatedUserProvider.getAuthenticatedUser();

        if(origin.getId().equals(destination.getId())){
            throw new ConflictedOriginDestinationException("Destination Airport and Destination Airport are the same");
        }

        Flight flight = flightMapper.toEntity(flightObject);
        flight.setOriginAirport(origin);
        flight.setDestinationAirport(destination);
        flight.setPrice(flightObject.getPrice());
        flight.setDepartureTime(flightObject.getDepartureTime());
        flight.setArrivalTime(flightObject.getArrivalTime());
        flight.setUser(user);

        flight = flightRepository.save(flight);
        return flightMapper.toResponse(flight);
    }

    public FlightResponse updateFlightByOriginAirport(Long airportId, Long flightId, FlightByOriginAirportRequest flightObject) {

        Flight flight = validateFlight(airportId, flightId);
        Airport destinationAirport = airportRepository.findById(flightObject.getDestinationAirportId()).orElseThrow(
                () -> new InformationNotFoundException("Destination Airport not found"));

        flight.setDestinationAirport(destinationAirport);
        flight.setDepartureTime(flightObject.getDepartureTime());
        flight.setArrivalTime(flightObject.getArrivalTime());
        flight.setPrice(flightObject.getPrice());
        flight =  flightRepository.save(flight);

        return flightMapper.toResponse(flight);
    }

    public FlightResponse updateFlight(Long flightId,FlightRequest flightObject) {

        Flight flight = flightRepository.findById(flightId).orElseThrow(
                () -> new InformationNotFoundException("Flight not found"));
        Airport originAirport = airportRepository.findById(flightObject.getOriginAirportId()).orElseThrow(
                () -> new InformationNotFoundException("Destination Airport not found"));
        Airport destinationAirport = airportRepository.findById(flightObject.getDestinationAirportId()).orElseThrow(
                () -> new InformationNotFoundException("Destination Airport not found"));

        flight.setOriginAirport(originAirport);
        flight.setDestinationAirport(destinationAirport);
        flight.setDepartureTime(flightObject.getDepartureTime());
        flight.setArrivalTime(flightObject.getArrivalTime());
        flight.setPrice(flightObject.getPrice());
        flight = flightRepository.save(flight);
        return  flightMapper.toResponse(flight);
    }

    public void deleteFlight(Long flightId) {

        Flight flight = flightRepository.findById(flightId).orElseThrow(
                () -> new InformationNotFoundException("Flight not found"));

        if(!flight.getBookings().isEmpty()){
            throw new FlightDeletionException("Cannot delete a flight with existing bookings. Resolve existing bookings before deletion.");
        };

        flightRepository.delete(flight);
    }

    public FlightResponse getFlight(Long flightId) {

        Flight flight = flightRepository.findById(flightId).orElseThrow(
                () -> new InformationNotFoundException("Flight not found")
        );

        return flightMapper.toResponse(flight);
    }

    public FlightResponse getFlightOfAirport(Long airportId, Long flightId) {

        Flight flight = validateFlight(airportId, flightId);

        return flightMapper.toResponse(flight);
    }

    public ListResponse<FlightResponse> getFlights(Pageable pageable) {

        Page<Flight> page = flightRepository.findAll(pageable);

        List<FlightResponse> data = page.stream().map(flightMapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);

    }

    public ListResponse<FlightResponse> searchFlights(
            Long id,
            String flightNo,
            Long originAirportId,
            String originAirportName,
            Long destinationAirportId,
            String destinationAirportName,
            BigDecimal price,
            String search,
            Pageable pageable
    ) {
        Specification<Flight> spec;
        
        boolean hasSpecificCriteria = id != null || 
            (flightNo != null && !flightNo.trim().isEmpty()) ||
            originAirportId != null || 
            (originAirportName != null && !originAirportName.trim().isEmpty()) ||
            destinationAirportId != null || 
            (destinationAirportName != null && !destinationAirportName.trim().isEmpty()) ||
            price != null;
        
        if (hasSpecificCriteria) {
            spec = FlightSpecification.withSearchCriteria(
                    id, flightNo, originAirportId, originAirportName,
                    destinationAirportId, destinationAirportName, price
            );
            if (search != null && !search.trim().isEmpty()) {
                spec = spec.and(FlightSpecification.withGeneralSearch(search));
            }
        } else if (search != null && !search.trim().isEmpty()) {
            spec = FlightSpecification.withGeneralSearch(search);
        } else {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        
        Page<Flight> page = flightRepository.findAll(spec, pageable);

        List<FlightResponse> data = page.stream().map(flightMapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<FlightResponse> getAirportFlights(Long airportId, Pageable pageable) {

        Airport airport = airportRepository.findById(airportId).orElseThrow(
                () -> new InformationNotFoundException("Airport not found"));

        Page<Flight> page = flightRepository.findByAirport(airport.getId(), pageable);
        List<FlightResponse> data =  page.stream().map(flightMapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<FlightResponse> getAirportDepartures(Long airportId,  Pageable pageable) {

        Airport airport = airportRepository.findById(airportId).orElseThrow(
                () -> new InformationNotFoundException("Airport not found"));

        Page<Flight> page = flightRepository.findByOriginAirport(airport.getId(), pageable);
        List<FlightResponse> data =  page.stream().map(flightMapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<FlightResponse> getAirportArrivals(Long airportId, Pageable pageable) {

        Airport airport = airportRepository.findById(airportId).orElseThrow(
                () -> new InformationNotFoundException("Airport not found"));

        Page<Flight> page = flightRepository.findByDestinationAirport(airport.getId(), pageable);
        List<FlightResponse> data =  page.stream().map(flightMapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);

        return  new ListResponse<>(data, meta);
    }

    private Flight validateFlight(Long airportId, Long flightId) {

        airportRepository.findById(airportId).orElseThrow(
                ()-> new InformationNotFoundException("Airport " + airportId + " not found"));

        Flight flight = flightRepository.findByIdAndAirport(airportId, flightId).orElseThrow(
                () -> new InformationNotFoundException("Flight " + flightId + " not found")
        );

        return flight;
    }

    public ListResponse<FlightResponse> browseFlights(
            Long originAirportId,
            Long destinationAirportId,
            LocalDateTime departureTimeFrom,
            LocalDateTime departureTimeTo,
            LocalDateTime arrivalTimeFrom,
            LocalDateTime arrivalTimeTo,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        // Apply default sorting by departureTime ascending if no sort is specified
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "departureTime")
            );
        }

        Specification<Flight> spec = FlightSpecification.withFutureFlightsFilter();

        Specification<Flight> browseSpec = FlightSpecification.withBrowseFilters(
                originAirportId,
                destinationAirportId,
                departureTimeFrom,
                departureTimeTo,
                arrivalTimeFrom,
                arrivalTimeTo,
                minPrice,
                maxPrice
        );

        spec = spec.and(browseSpec);

        Page<Flight> page = flightRepository.findAll(spec, pageable);
        List<FlightResponse> data = page.stream().map(flightMapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);
        return new ListResponse<>(data, meta);
    }
}
