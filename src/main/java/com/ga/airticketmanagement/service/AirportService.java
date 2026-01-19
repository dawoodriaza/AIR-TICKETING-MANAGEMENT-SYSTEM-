package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.AirportMapper;
import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.request.AirportRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.dto.response.AirportResponse;
import com.ga.airticketmanagement.exception.InformationFoundException;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Airport;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.AirportRepository;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AirportService {

    private final AirportMapper airportMapper;
    private final AirportRepository airportRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    public AirportService(AirportMapper airportMapper, AirportRepository airportRepository,
                          AuthenticatedUserProvider authenticatedUserProvider) {
        this.airportMapper = airportMapper;
        this.airportRepository = airportRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public AirportResponse createAirport(AirportRequest airportObject){

        User user = authenticatedUserProvider.getAuthenticatedUser();

        Airport airport = airportMapper.toEntity(airportObject);
        airportRepository.findByName(airportObject.getName())
                .ifPresent(
                a -> {
                    throw new InformationFoundException("Airport with name " + airportObject.getName() + " already exists");
                });

        airport.setUser(user);
        airport = airportRepository.save(airport);
        return airportMapper.toResponse(airport);
    }

    public AirportResponse getAirport(Long id){

        Airport airport = airportRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Airport with id " + id + " not found")
        );

        return  airportMapper.toResponse(airport);
    }


    public ListResponse<AirportResponse> getAirports(Pageable pageable) {

        Page<Airport> page = airportRepository.findAll(pageable);

        List<AirportResponse> data = page.getContent().stream()
                .map(airportMapper::toResponse).toList();

        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public AirportResponse updateAirport(Long id, AirportRequest airportObject){

        Airport airport = airportRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Airport " + id + " does not exist.")
        );

       airportRepository.findByName(airportObject.getName()).ifPresent(
            existAirport -> {
                if (!existAirport.getId().equals(id)) {
                    throw new InformationFoundException("Airport with name " + airportObject.getName() + " already exists");
                }
            });

        airport.setName(airportObject.getName());
        airport.setCountry(airportObject.getCountry());
        airport.setCode(airportObject.getCode());
        Airport updatedAirport = airportRepository.save(airport);

        return airportMapper.toResponse(updatedAirport);
    }


    public void deleteAirport(Long id){

        Airport airport = airportRepository.findById(id).orElseThrow(
                () -> new InformationNotFoundException("Airport " + id + " does not exist")
        );

        airportRepository.delete(airport);
    }
}
