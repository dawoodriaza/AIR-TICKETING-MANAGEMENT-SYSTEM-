package com.ga.airticketmanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightResponse(
        Long id,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        BigDecimal price,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long originAirportId,
        Long destinationAirportId,
        Long userId
) {}
