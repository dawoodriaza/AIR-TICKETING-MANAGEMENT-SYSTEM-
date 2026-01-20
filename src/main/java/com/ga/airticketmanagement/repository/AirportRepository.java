package com.ga.airticketmanagement.repository;

import com.ga.airticketmanagement.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRepository extends JpaRepository<Airport, Long>, JpaSpecificationExecutor<Airport> {

    Optional<Airport> findByName(String name);

    Airport findByCode(String code);
}
