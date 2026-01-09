package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getBookings());
    }



    @PostMapping("/api/createbooking")
    public Booking createBooking(@RequestBody Booking bookingObject ){
        System.out.println("Service Calling this method ==>");
        return bookingService.createBooking(bookingObject);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking
    ) {
        return ResponseEntity.ok(bookingService.updateBookingById(id, booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBookingById(id);
        return ResponseEntity.ok("Booking with ID " + id + " deleted successfully.");
    }
}
