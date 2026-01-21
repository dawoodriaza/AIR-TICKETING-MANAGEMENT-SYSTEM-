package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.response.AdminStatsResponse;
import com.ga.airticketmanagement.model.BookingStatus;
import com.ga.airticketmanagement.repository.BookingRepository;
import com.ga.airticketmanagement.repository.FlightRepository;
import com.ga.airticketmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public AdminStatsResponse getStats() {
        Long totalBookings = bookingRepository.count();
        Long totalFlights = flightRepository.count();
        Long totalUsers = userRepository.count();
        // Revenue is sum of flight prices from confirmed bookings
        BigDecimal totalRevenueBigDecimal = bookingRepository.sumFlightPriceByStatus(BookingStatus.CONFIRMED);
        Double totalRevenue = totalRevenueBigDecimal != null ? totalRevenueBigDecimal.doubleValue() : 0.0;

        return new AdminStatsResponse(
                totalBookings,
                totalFlights,
                totalUsers,
                totalRevenue
        );
    }
}
