package com.ga.airticketmanagement.seeder;

import com.ga.airticketmanagement.model.Airport;
import com.ga.airticketmanagement.model.Flight;
import com.ga.airticketmanagement.repository.AirportRepository;
import com.ga.airticketmanagement.repository.FlightRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightSeeder {

    private static final Logger logger = LoggerFactory.getLogger(FlightSeeder.class);

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private AirportRepository airportRepository;

    public void seed() {
        logger.info("✈️  Seeding flights...");

        if (flightRepository.count() > 0) {
            logger.info("⏭️  Flights already exist (count: {}), skipping flight seeding", flightRepository.count());
            return;
        }

        // Check if airports exist
        if (airportRepository.count() == 0) {
            logger.error("❌ No airports found! Make sure airports are seeded first.");
            return;
        }

        // Get airports by code
        Airport jfk = airportRepository.findByCode("JFK");
        Airport lhr = airportRepository.findByCode("LHR");
        Airport dxb = airportRepository.findByCode("DXB");
        Airport sin = airportRepository.findByCode("SIN");
        Airport nrt = airportRepository.findByCode("NRT");
        Airport lax = airportRepository.findByCode("LAX");
        Airport cdg = airportRepository.findByCode("CDG");
        Airport fco = airportRepository.findByCode("FCO");
        Airport syd = airportRepository.findByCode("SYD");
        Airport bkk = airportRepository.findByCode("BKK");
        Airport yyz = airportRepository.findByCode("YYZ");
        Airport mex = airportRepository.findByCode("MEX");
        Airport txl = airportRepository.findByCode("TXL");
        Airport ist = airportRepository.findByCode("IST");
        Airport bom = airportRepository.findByCode("BOM");

        List<Flight> flights = new ArrayList<>();

        // Flight 1: New York (JFK) to London (LHR)
        if (jfk != null && lhr != null) {
            flights.add(createFlight(
                    jfk, lhr,
                    LocalDateTime.now().plusDays(5).withHour(10).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(5).withHour(22).withMinute(30).withSecond(0),
                    new BigDecimal("599.99")
            ));
        }

        // Flight 2: London (LHR) to Dubai (DXB)
        if (lhr != null && dxb != null) {
            flights.add(createFlight(
                    lhr, dxb,
                    LocalDateTime.now().plusDays(7).withHour(14).withMinute(30).withSecond(0),
                    LocalDateTime.now().plusDays(8).withHour(0).withMinute(45).withSecond(0),
                    new BigDecimal("749.99")
            ));
        }

        // Flight 3: Dubai (DXB) to Singapore (SIN)
        if (dxb != null && sin != null) {
            flights.add(createFlight(
                    dxb, sin,
                    LocalDateTime.now().plusDays(10).withHour(2).withMinute(15).withSecond(0),
                    LocalDateTime.now().plusDays(10).withHour(13).withMinute(30).withSecond(0),
                    new BigDecimal("899.99")
            ));
        }

        // Flight 4: Singapore (SIN) to Tokyo (NRT)
        if (sin != null && nrt != null) {
            flights.add(createFlight(
                    sin, nrt,
                    LocalDateTime.now().plusDays(12).withHour(8).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(12).withHour(16).withMinute(15).withSecond(0),
                    new BigDecimal("649.99")
            ));
        }

        // Flight 5: Los Angeles (LAX) to Paris (CDG)
        if (lax != null && cdg != null) {
            flights.add(createFlight(
                    lax, cdg,
                    LocalDateTime.now().plusDays(3).withHour(19).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(4).withHour(14).withMinute(30).withSecond(0),
                    new BigDecimal("699.99")
            ));
        }

        // Flight 6: Paris (CDG) to Rome (FCO)
        if (cdg != null && fco != null) {
            flights.add(createFlight(
                    cdg, fco,
                    LocalDateTime.now().plusDays(15).withHour(11).withMinute(30).withSecond(0),
                    LocalDateTime.now().plusDays(15).withHour(13).withMinute(45).withSecond(0),
                    new BigDecimal("199.99")
            ));
        }

        // Flight 7: Sydney (SYD) to Bangkok (BKK)
        if (syd != null && bkk != null) {
            flights.add(createFlight(
                    syd, bkk,
                    LocalDateTime.now().plusDays(8).withHour(22).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(9).withHour(5).withMinute(30).withSecond(0),
                    new BigDecimal("549.99")
            ));
        }

        // Flight 8: Toronto (YYZ) to Mexico City (MEX)
        if (yyz != null && mex != null) {
            flights.add(createFlight(
                    yyz, mex,
                    LocalDateTime.now().plusDays(6).withHour(7).withMinute(30).withSecond(0),
                    LocalDateTime.now().plusDays(6).withHour(13).withMinute(15).withSecond(0),
                    new BigDecimal("399.99")
            ));
        }

        // Flight 9: Berlin (TXL) to Istanbul (IST)
        if (txl != null && ist != null) {
            flights.add(createFlight(
                    txl, ist,
                    LocalDateTime.now().plusDays(2).withHour(16).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(2).withHour(20).withMinute(30).withSecond(0),
                    new BigDecimal("299.99")
            ));
        }

        // Flight 10: Mumbai (BOM) to London (LHR)
        if (bom != null && lhr != null) {
            flights.add(createFlight(
                    bom, lhr,
                    LocalDateTime.now().plusDays(4).withHour(3).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(4).withHour(8).withMinute(30).withSecond(0),
                    new BigDecimal("799.99")
            ));
        }

        // Flight 11: Tokyo (NRT) to Los Angeles (LAX)
        if (nrt != null && lax != null) {
            flights.add(createFlight(
                    nrt, lax,
                    LocalDateTime.now().plusDays(9).withHour(11).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(9).withHour(5).withMinute(30).withSecond(0),
                    new BigDecimal("899.99")
            ));
        }

        // Flight 12: Dubai (DXB) to New York (JFK)
        if (dxb != null && jfk != null) {
            flights.add(createFlight(
                    dxb, jfk,
                    LocalDateTime.now().plusDays(11).withHour(8).withMinute(15).withSecond(0),
                    LocalDateTime.now().plusDays(11).withHour(14).withMinute(45).withSecond(0),
                    new BigDecimal("949.99")
            ));
        }

        // Flight 13: Bangkok (BKK) to Singapore (SIN)
        if (bkk != null && sin != null) {
            flights.add(createFlight(
                    bkk, sin,
                    LocalDateTime.now().plusDays(13).withHour(15).withMinute(30).withSecond(0),
                    LocalDateTime.now().plusDays(13).withHour(17).withMinute(45).withSecond(0),
                    new BigDecimal("149.99")
            ));
        }

        // Flight 14: Rome (FCO) to Istanbul (IST)
        if (fco != null && ist != null) {
            flights.add(createFlight(
                    fco, ist,
                    LocalDateTime.now().plusDays(14).withHour(9).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(14).withHour(12).withMinute(30).withSecond(0),
                    new BigDecimal("259.99")
            ));
        }

        // Flight 15: Mexico City (MEX) to Los Angeles (LAX)
        if (mex != null && lax != null) {
            flights.add(createFlight(
                    mex, lax,
                    LocalDateTime.now().plusDays(16).withHour(13).withMinute(0).withSecond(0),
                    LocalDateTime.now().plusDays(16).withHour(16).withMinute(30).withSecond(0),
                    new BigDecimal("349.99")
            ));
        }

        // Save all flights
        flightRepository.saveAll(flights);

        logger.info("✅ Created {} flights", flights.size());
        logger.info("   Routes: JFK→LHR, LHR→DXB, DXB→SIN, SIN→NRT, LAX→CDG");
        logger.info("   Routes: CDG→FCO, SYD→BKK, YYZ→MEX, TXL→IST, BOM→LHR");
        logger.info("   Routes: NRT→LAX, DXB→JFK, BKK→SIN, FCO→IST, MEX→LAX");
    }

    private Flight createFlight(Airport origin, Airport destination,
                                LocalDateTime departureTime, LocalDateTime arrivalTime,
                                BigDecimal price) {
        Flight flight = new Flight();
        flight.setOriginAirport(origin);
        flight.setDestinationAirport(destination);
        flight.setDepartureTime(departureTime);
        flight.setArrivalTime(arrivalTime);
        flight.setPrice(price);
        return flight;
    }
}