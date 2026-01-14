package com.ga.airticketmanagement.seeder;

import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.Flight;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.BookingRepository;
import com.ga.airticketmanagement.repository.FlightRepository;
import com.ga.airticketmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class BookingSeeder {

    private static final Logger logger = LoggerFactory.getLogger(BookingSeeder.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    public void seed() {
        logger.info("📋 Seeding bookings...");

        if (bookingRepository.count() > 0) {
            logger.info("⏭️  Bookings already exist (count: {}), skipping booking seeding", bookingRepository.count());
            return;
        }

        // Get seeded users
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.error("❌ No users found! Make sure users are seeded first.");
            return;
        }

        // Get seeded flights
        List<Flight> flights = flightRepository.findAll();
        if (flights.isEmpty()) {
            logger.error("❌ No flights found! Make sure flights are seeded first.");
            return;
        }

        // Get specific users for bookings
         Optional<User> admin = userRepository.findUserByEmailAddress("admin@airticket.com");
        Optional<User> john = userRepository.findUserByEmailAddress("john.doe@example.com");
        Optional<User> jane = userRepository.findUserByEmailAddress("jane.smith@example.com");
        Optional<User> mike = userRepository.findUserByEmailAddress("mike.johnson@example.com");

        List<Booking> bookings = new ArrayList<>();

        // Booking 1: Admin books first flight
        if (admin != null && flights.size() > 0) {
            bookings.add(createBooking(
                    "Admin Super",
                    2,
                    flights.get(0).getPrice().floatValue() * 2,
                    "CONFIRMED",
                    admin,
                    flights.get(0)
            ));
        }

        // Booking 2: John books second flight
        if (john != null && flights.size() > 1) {
            bookings.add(createBooking(
                    "John Doe",
                    1,
                    flights.get(1).getPrice().floatValue(),
                    "CONFIRMED",
                    john,
                    flights.get(1)
            ));
        }

        // Booking 3: Jane books third flight
        if (jane != null && flights.size() > 2) {
            bookings.add(createBooking(
                    "Jane Smith",
                    1,
                    flights.get(2).getPrice().floatValue(),
                    "CONFIRMED",
                    jane,
                    flights.get(2)
            ));
        }

        // Booking 4: Mike books fourth flight - Pending
        if (mike != null && flights.size() > 3) {
            bookings.add(createBooking(
                    "Mike Johnson",
                    3,
                    flights.get(3).getPrice().floatValue() * 3,
                    "PENDING",
                    mike,
                    flights.get(3)
            ));
        }

        // Booking 5: John books another flight
        if (john != null && flights.size() > 4) {
            bookings.add(createBooking(
                    "John Doe",
                    1,
                    flights.get(4).getPrice().floatValue(),
                    "CONFIRMED",
                    john,
                    flights.get(4)
            ));
        }

        // Booking 6: Jane books another - Cancelled
        if (jane != null && flights.size() > 5) {
            bookings.add(createBooking(
                    "Jane Smith",
                    1,
                    flights.get(5).getPrice().floatValue(),
                    "CANCELLED",
                    jane,
                    flights.get(5)
            ));
        }

        // Booking 7: Admin books multiple passengers
        if (admin != null && flights.size() > 6) {
            bookings.add(createBooking(
                    "Admin Super",
                    4,
                    flights.get(6).getPrice().floatValue() * 4,
                    "CONFIRMED",
                    admin,
                    flights.get(6)
            ));
        }

        // Booking 8: Mike books another - Confirmed
        if (mike != null && flights.size() > 7) {
            bookings.add(createBooking(
                    "Mike Johnson",
                    2,
                    flights.get(7).getPrice().floatValue() * 2,
                    "CONFIRMED",
                    mike,
                    flights.get(7)
            ));
        }

        // Booking 9: John books family trip
        if (john != null && flights.size() > 8) {
            bookings.add(createBooking(
                    "John Doe",
                    4,
                    flights.get(8).getPrice().floatValue() * 4,
                    "CONFIRMED",
                    john,
                    flights.get(8)
            ));
        }

        // Booking 10: Jane books solo trip
        if (jane != null && flights.size() > 9) {
            bookings.add(createBooking(
                    "Jane Smith",
                    1,
                    flights.get(9).getPrice().floatValue(),
                    "CONFIRMED",
                    jane,
                    flights.get(9)
            ));
        }

        // Booking 11: Admin books business trip
        if (admin != null && flights.size() > 10) {
            bookings.add(createBooking(
                    "Admin Super",
                    1,
                    flights.get(10).getPrice().floatValue(),
                    "CONFIRMED",
                    admin,
                    flights.get(10)
            ));
        }

        // Booking 12: Mike books couple trip - Pending payment
        if (mike != null && flights.size() > 11) {
            bookings.add(createBooking(
                    "Mike Johnson",
                    2,
                    flights.get(11).getPrice().floatValue() * 2,
                    "PENDING",
                    mike,
                    flights.get(11)
            ));
        }

        // Save all bookings
        bookingRepository.saveAll(bookings);

        logger.info("✅ Created {} bookings", bookings.size());

        // Count by status
        long confirmed = bookings.stream().filter(b -> "CONFIRMED".equals(b.getStatus())).count();
        long pending = bookings.stream().filter(b -> "PENDING".equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> "CANCELLED".equals(b.getStatus())).count();

        logger.info("   - {} CONFIRMED bookings", confirmed);
        logger.info("   - {} PENDING bookings", pending);
        logger.info("   - {} CANCELLED bookings", cancelled);
        logger.info("   Total passengers: {}", bookings.stream().mapToInt(Booking::getNumberOfPassengers).sum());
    }

    private Booking createBooking(String name, int numberOfPassengers,
                                  float totalPrice, String status,
                                  Optional<User> user, Flight flight) {
        Booking booking = new Booking();
        user.ifPresent(user1 -> {


            booking.setName(name);
            booking.setNumberOfPassengers(numberOfPassengers);
            booking.setTotal_price(totalPrice);
            booking.setStatus(status);
            booking.setUser(user1);
            booking.setFlight(flight);



        });
        return booking;


        // createdAt and booking_date will be set automatically by @CreationTimestamp


    }
}