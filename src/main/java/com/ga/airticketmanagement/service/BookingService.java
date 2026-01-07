package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getBookings() {
        return bookingRepository.findAll();
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new InformationNotFoundException("Booking with Id " + bookingId + " not found"));
    }

    public Booking updateBookingById(Long id, Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Request body is missing");
        }

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    existingBooking.setTotal_price(booking.getTotal_price());
                    existingBooking.setStatus(booking.getStatus());
                    existingBooking.setBooking_date(LocalDateTime.now());
                    existingBooking.setNumberOfPassengers(booking.getNumberOfPassengers());
                    return bookingRepository.save(existingBooking);
                })
                .orElseThrow(() ->
                        new InformationNotFoundException("Booking with Id " + id + " not found"));
    }
    public Booking createBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Request body is missing");
        }

        booking.setBooking_date(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public void deleteBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException("Booking with Id " + id + " not found"));
        bookingRepository.delete(booking);
    }



}
