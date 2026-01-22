package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.BookingMapper;
import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.request.BookingRequest;
import com.ga.airticketmanagement.dto.response.BookingResponse;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.BookingStatus;
import com.ga.airticketmanagement.model.Flight;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.BookingRepository;
import com.ga.airticketmanagement.repository.FlightRepository;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import com.ga.airticketmanagement.specification.BookingSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper mapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final FlightRepository flightRepository;

    public ListResponse<BookingResponse> getBookings(Pageable pageable) {
        Specification<Booking> spec = needsFlightJoin(pageable) 
            ? BookingSpecification.withFlightJoin() 
            : (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        
        Page<Booking> page = bookingRepository.findAll(spec, pageable);
        List<BookingResponse> data = page.getContent().stream()
                .map(mapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);
        return new ListResponse<>(data, meta);
    }

    public ListResponse<BookingResponse> searchBookings(
            Long id,
            Long flightId,
            Long userId,
            String search,
            Pageable pageable
    ) {
        Specification<Booking> spec;
        
        boolean hasSpecificCriteria = id != null || 
            flightId != null || 
            userId != null;
        
        if (hasSpecificCriteria) {
            spec = BookingSpecification.withSearchCriteria(
                    id, flightId, userId
            );
            if (search != null && !search.trim().isEmpty()) {
                spec = spec.and(BookingSpecification.withGeneralSearch(search));
            }
        } else if (search != null && !search.trim().isEmpty()) {
            spec = BookingSpecification.withGeneralSearch(search);
        } else {
            spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        
        if (needsFlightJoin(pageable)) {
            spec = spec.and(BookingSpecification.withFlightJoin());
        }
        
        Page<Booking> page = bookingRepository.findAll(spec, pageable);

        List<BookingResponse> data = page.getContent().stream()
                .map(mapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<BookingResponse> getBookingsByUserId(Long userId, Pageable pageable) {
        Specification<Booking> spec = BookingSpecification.withSearchCriteria(null, null, userId);
        
        if (needsFlightJoin(pageable)) {
            spec = spec.and(BookingSpecification.withFlightJoin());
        }
        
        Page<Booking> page = bookingRepository.findAll(spec, pageable);
        List<BookingResponse> data = page.getContent().stream()
                .map(mapper::toResponse).toList();
        PageMeta meta = PageMetaFactory.from(page);
        return new ListResponse<>(data, meta);
    }

    private boolean needsFlightJoin(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return false;
        }
        
        return pageable.getSort().stream()
            .anyMatch(order -> order.getProperty().startsWith("flight."));
    }

    public BookingResponse getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new InformationNotFoundException("Booking with Id " + bookingId + " not found"));
        return mapper.toResponse(booking);
    }

    @Transactional
    public BookingResponse updateBookingById(Long id, BookingRequest bookingRequest) {
        log.debug(bookingRequest.getStatus().toString());
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException("Booking with Id " + id + " not found"));

        if (bookingRequest.getStatus() != null) {
            booking.setStatus(bookingRequest.getStatus());
        }

        if (bookingRequest.getFlightId() != null) {
            Flight flight = flightRepository.findById(bookingRequest.getFlightId())
                    .orElseThrow(() -> new InformationNotFoundException("Flight not found"));
            booking.setFlight(flight);
        }

        Booking updated = bookingRepository.save(booking);
        return mapper.toResponse(updated);
    }

    @Transactional
    public void deleteBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() ->
                        new InformationNotFoundException("Booking with Id " + id + " not found"));
        bookingRepository.delete(booking);
    }

    @Transactional
    public BookingResponse create(BookingRequest dto) {
        Booking booking = mapper.toEntity(dto);
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        booking.setUser(currentUser);

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new InformationNotFoundException("Flight not found"));
        booking.setFlight(flight);

        if (dto.getStatus() == null) {
            booking.setStatus(BookingStatus.PENDING);
        } else {
            booking.setStatus(dto.getStatus());
        }

        Booking saved = bookingRepository.save(booking);
        return mapper.toResponse(saved);
    }
}