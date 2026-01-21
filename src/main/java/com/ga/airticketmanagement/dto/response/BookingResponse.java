package com.ga.airticketmanagement.dto.response;

import com.ga.airticketmanagement.model.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long userId,
        BookingStatus status,
        FlightResponse flight,
        LocalDateTime bookedAt,
        LocalDateTime updatedAt
) {}
