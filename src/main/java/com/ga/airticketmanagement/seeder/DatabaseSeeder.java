package com.ga.airticketmanagement.seeder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

    @Value("${app.seeding.enabled:true}")
    private boolean seedingEnabled;

    @Autowired
    private UserSeeder userSeeder;

    @Autowired
    private AirportSeeder airportSeeder;

    @Autowired
    private FlightSeeder flightSeeder;

    @Autowired
    private BookingSeeder bookingSeeder;

    @Override
    public void run(String... args) throws Exception {
        if (!seedingEnabled) {
            logger.info("========================================");
            logger.info("⏭️  DATABASE SEEDING IS DISABLED");
            logger.info("========================================");
            return;
        }

        logger.info("========================================");
        logger.info("🌱 STARTING DATABASE SEEDING");
        logger.info("========================================");

        try {

            userSeeder.seed();
            airportSeeder.seed();
            flightSeeder.seed();
            bookingSeeder.seed();

            logger.info("========================================");
            logger.info("✅ DATABASE SEEDING COMPLETED SUCCESSFULLY!");
            logger.info("========================================");

        } catch (Exception e) {
            logger.error("========================================");
            logger.error("❌ DATABASE SEEDING FAILED!");
            logger.error("Error: {}", e.getMessage());
            logger.error("========================================", e);
            throw e;
        }
    }
}