package com.ga.airticketmanagement.repository;

import com.ga.airticketmanagement.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {

    Flight findByIdAndOriginAirportId(Long id, Long originAirportId);
    Flight findByIdAndDestinationAirportId(Long id, Long destinationAirportId);
    @Query("SELECT f FROM Flight f WHERE f.id = :flightId AND (f.originAirport.id = :airportId OR f.destinationAirport.id = :airportId)")
    Optional<Flight> findByIdAndAirport(@Param("airportId") Long airportId, @Param("flightId") Long flightId);
    @Query("SELECT f FROM Flight f WHERE f.originAirport.id = :airportId OR f.destinationAirport.id = :airportId")
    List<Flight> findByAirport(@Param("airportId") Long airportId);
    @Query("SELECT f FROM Flight f WHERE f.originAirport.id = :airportId OR f.destinationAirport.id = :airportId")
    Page<Flight> findByAirport(@Param("airportId") Long airportId, Pageable pageable);
    @Query("SELECT f FROM Flight f WHERE f.originAirport.id = :airportId")
    Page<Flight> findByOriginAirport(@Param("airportId") Long airportId, Pageable pageable);
    @Query("SELECT f FROM Flight f WHERE f.destinationAirport.id = :airportId")
    Page<Flight> findByDestinationAirport(@Param("airportId") Long airportId, Pageable pageable);
    @Query("SELECT f FROM Flight f WHERE f.departureTime > :currentTime ORDER BY f.departureTime ASC")
    Page<Flight> findFutureFlights(@Param("currentTime") LocalDateTime currentTime, Pageable pageable);
    Optional<Flight> findByFlightNo(String flightNo);
}
