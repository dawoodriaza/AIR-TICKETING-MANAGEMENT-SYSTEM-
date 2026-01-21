package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.BookingRequest;
import com.ga.airticketmanagement.dto.response.BookingResponse;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    public ListResponse<BookingResponse> getAllBookings(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Long flightId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        
        if (id != null || flightId != null || 
            userId != null ||
            (search != null && !search.trim().isEmpty())) {
            return bookingService.searchBookings(id, flightId, userId, search, pageable);
        }
        
        return bookingService.getBookings(pageable);
    }

    @GetMapping("/{id}")
    public BookingResponse getBookingById(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PostMapping
    public BookingResponse create(@RequestBody BookingRequest dto) {
        return bookingService.create(dto);
    }

    @PutMapping("/{id}")
    public BookingResponse updateBooking(
            @PathVariable Long id,
            @RequestBody BookingRequest bookingRequest
    ) {
        return bookingService.updateBookingById(id, bookingRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBookingById(id);
    }
}
