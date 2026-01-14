package com.ga.airticketmanagement.seeder;

import com.ga.airticketmanagement.model.Airport;
import com.ga.airticketmanagement.repository.AirportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AirportSeeder {

    private static final Logger logger = LoggerFactory.getLogger(AirportSeeder.class);

    @Autowired
    private AirportRepository airportRepository;

    public void seed() {
        logger.info("🛫 Seeding airports...");

        if (airportRepository.count() > 0) {
            logger.info("⏭️  Airports already exist (count: {}), skipping airport seeding", airportRepository.count());
            return;
        }

        List<Airport> airports = new ArrayList<>();

        // North America
        airports.add(createAirport("John F. Kennedy International Airport", "United States", "JFK"));
        airports.add(createAirport("Los Angeles International Airport", "United States", "LAX"));
        airports.add(createAirport("Toronto Pearson International Airport", "Canada", "YYZ"));
        airports.add(createAirport("Mexico City International Airport", "Mexico", "MEX"));

        // Europe
        airports.add(createAirport("Heathrow Airport", "United Kingdom", "LHR"));
        airports.add(createAirport("Charles de Gaulle Airport", "France", "CDG"));
        airports.add(createAirport("Leonardo da Vinci–Fiumicino Airport", "Italy", "FCO"));
        airports.add(createAirport("Berlin Tegel Airport", "Germany", "TXL"));
        airports.add(createAirport("Istanbul Airport", "Turkey", "IST"));

        // Middle East
        airports.add(createAirport("Dubai International Airport", "UAE", "DXB"));
        airports.add(createAirport("Chhatrapati Shivaji Maharaj International Airport", "India", "BOM"));

        // Asia
        airports.add(createAirport("Singapore Changi Airport", "Singapore", "SIN"));
        airports.add(createAirport("Narita International Airport", "Japan", "NRT"));
        airports.add(createAirport("Suvarnabhumi Airport", "Thailand", "BKK"));

        // Oceania
        airports.add(createAirport("Sydney Kingsford Smith Airport", "Australia", "SYD"));

        // Save all airports
        airportRepository.saveAll(airports);

        logger.info("✅ Created {} airports", airports.size());
        logger.info("   North America: JFK, LAX, YYZ, MEX");
        logger.info("   Europe: LHR, CDG, FCO, TXL, IST");
        logger.info("   Middle East: DXB, BOM");
        logger.info("   Asia: SIN, NRT, BKK");
        logger.info("   Oceania: SYD");
    }

    private Airport createAirport(String name, String country, String code) {
        Airport airport = new Airport();
        airport.setName(name);
        airport.setCountry(country);
        airport.setCode(code);
        return airport;
    }
}