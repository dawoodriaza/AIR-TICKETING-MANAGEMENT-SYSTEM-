package com.ga.airticketmanagement.seeder;

import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.BookingStatus;
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

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            logger.error("❌ No users found! Make sure users are seeded first.");
            return;
        }

        List<Flight> flights = flightRepository.findAll();
        if (flights.isEmpty()) {
            logger.error("❌ No flights found! Make sure flights are seeded first.");
            return;
        }

        Optional<User> admin = userRepository.findUserByEmailAddress("admin@airticket.com");
        Optional<User> john = userRepository.findUserByEmailAddress("john.doe@example.com");
        Optional<User> jane = userRepository.findUserByEmailAddress("jane.smith@example.com");
        Optional<User> mike = userRepository.findUserByEmailAddress("mike.johnson@example.com");

        List<Booking> bookings = new ArrayList<>();

        if (admin.isPresent() && flights.size() > 0) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, admin.get(), flights.get(0)));
        }

        if (john.isPresent() && flights.size() > 1) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, john.get(), flights.get(1)));
        }

        if (jane.isPresent() && flights.size() > 2) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, jane.get(), flights.get(2)));
        }

        if (mike.isPresent() && flights.size() > 3) {
            bookings.add(createBooking(BookingStatus.PENDING, mike.get(), flights.get(3)));
        }

        if (john.isPresent() && flights.size() > 4) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, john.get(), flights.get(4)));
        }

        if (jane.isPresent() && flights.size() > 5) {
            bookings.add(createBooking(BookingStatus.CANCELLED, jane.get(), flights.get(5)));
        }

        if (admin.isPresent() && flights.size() > 6) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, admin.get(), flights.get(6)));
        }

        if (mike.isPresent() && flights.size() > 7) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, mike.get(), flights.get(7)));
        }

        if (john.isPresent() && flights.size() > 8) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, john.get(), flights.get(8)));
        }

        if (jane.isPresent() && flights.size() > 9) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, jane.get(), flights.get(9)));
        }

        if (admin.isPresent() && flights.size() > 10) {
            bookings.add(createBooking(BookingStatus.CONFIRMED, admin.get(), flights.get(10)));
        }

        if (mike.isPresent() && flights.size() > 11) {
            bookings.add(createBooking(BookingStatus.PENDING, mike.get(), flights.get(11)));
        }

        bookingRepository.saveAll(bookings);

        logger.info("✅ Created {} bookings", bookings.size());

        long confirmed = bookings.stream().filter(b -> BookingStatus.CONFIRMED.equals(b.getStatus())).count();
        long pending = bookings.stream().filter(b -> BookingStatus.PENDING.equals(b.getStatus())).count();
        long cancelled = bookings.stream().filter(b -> BookingStatus.CANCELLED.equals(b.getStatus())).count();

        logger.info("   - {} CONFIRMED bookings", confirmed);
        logger.info("   - {} PENDING bookings", pending);
        logger.info("   - {} CANCELLED bookings", cancelled);
    }

    private Booking createBooking(BookingStatus status, User user, Flight flight) {
        Booking booking = new Booking();
        booking.setStatus(status);
        booking.setUser(user);
        booking.setFlight(flight);
        return booking;
    }
}