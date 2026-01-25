package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.mapper.PageMetaFactory;
import com.ga.airticketmanagement.dto.mapper.PaymentMapper;
import com.ga.airticketmanagement.dto.request.PaymentRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PageMeta;
import com.ga.airticketmanagement.dto.response.PaymentResponse;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.BookingStatus;
import com.ga.airticketmanagement.model.Payment;
import com.ga.airticketmanagement.model.User;
import com.ga.airticketmanagement.repository.BookingRepository;
import com.ga.airticketmanagement.repository.PaymentRepo;
import com.ga.airticketmanagement.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.debug("Creating payment for booking ID: {}", request.getBookingId());

        User currentUser = authenticatedUserProvider.getAuthenticatedUser();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new InformationNotFoundException(
                        "Booking with ID " + request.getBookingId() + " not found"));

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You can only pay for your own bookings");
        }

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("This booking has already been paid");
        }

        if (paymentRepository.existsByBookingId(booking.getId())) {
            throw new IllegalStateException("Payment already exists for this booking");
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setUser(currentUser);
        payment.setBooking(booking);
        payment.setStatus("SUCCESS");
        payment.setTransactionRef("DAWOOD-" + System.currentTimeMillis() + "-" + booking.getId());

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully. Transaction Ref: {}", savedPayment.getTransactionRef());

        return paymentMapper.toResponse(savedPayment);
    }

    public ListResponse<PaymentResponse> getAllPayments(Pageable pageable) {
        log.debug("Fetching all payments");

        Page<Payment> page = paymentRepository.findAll(pageable);
        List<PaymentResponse> data = page.getContent().stream()
                .map(paymentMapper::toResponse)
                .toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<PaymentResponse> getPaymentsByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching payments for user ID: {}", userId);

        Page<Payment> page = paymentRepository.findByUserId(userId, pageable);
        List<PaymentResponse> data = page.getContent().stream()
                .map(paymentMapper::toResponse)
                .toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<PaymentResponse> getPaymentsByBookingId(Long bookingId, Pageable pageable) {
        log.debug("Fetching payments for booking ID: {}", bookingId);

        Page<Payment> page = paymentRepository.findByBookingId(bookingId, pageable);
        List<PaymentResponse> data = page.getContent().stream()
                .map(paymentMapper::toResponse)
                .toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<PaymentResponse> getPaymentsByStatus(String status, Pageable pageable) {
        log.debug("Fetching payments with status: {}", status);

        Page<Payment> page = paymentRepository.findByStatus(status, pageable);
        List<PaymentResponse> data = page.getContent().stream()
                .map(paymentMapper::toResponse)
                .toList();
        PageMeta meta = PageMetaFactory.from(page);

        return new ListResponse<>(data, meta);
    }

    public ListResponse<PaymentResponse> getCurrentUserPayments(Pageable pageable) {
        User currentUser = authenticatedUserProvider.getAuthenticatedUser();
        log.debug("Fetching payments for current user: {}", currentUser.getEmailAddress());
        return getPaymentsByUserId(currentUser.getId(), pageable);
    }

    public PaymentResponse getPaymentById(Long id) {
        log.debug("Fetching payment with ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException(
                        "Payment with ID " + id + " not found"));

        return paymentMapper.toResponse(payment);
    }

    public PaymentResponse getPaymentByTransactionRef(String transactionRef) {
        log.debug("Fetching payment with transaction ref: {}", transactionRef);

        Payment payment = paymentRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new InformationNotFoundException(
                        "Payment with transaction reference " + transactionRef + " not found"));

        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse updatePayment(Long id, PaymentRequest request) {
        log.debug("Updating payment ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException(
                        "Payment with ID " + id + " not found"));

        if (request.getAmount() != null) {
            payment.setAmount(request.getAmount());
        }

        Payment updated = paymentRepository.save(payment);
        log.info("Payment updated successfully: {}", id);

        return paymentMapper.toResponse(updated);
    }

    @Transactional
    public PaymentResponse refundPayment(Long id) {
        log.debug("Refunding payment ID: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException(
                        "Payment with ID " + id + " not found"));

        if (!"SUCCESS".equals(payment.getStatus())) {
            throw new IllegalStateException("Only successful payments can be refunded");
        }

        payment.setStatus("REFUNDED");

        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        Payment refunded = paymentRepository.save(payment);
        log.info("Payment refunded successfully: {}", id);

        return paymentMapper.toResponse(refunded);
    }

    @Transactional
    public void deletePayment(Long id) {
        log.debug("Deleting payment id: {}", id);

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new InformationNotFoundException(
                        "Payment with id " + id + " not found"));

        paymentRepository.delete(payment);
        log.info("Payment deleted successfully: {}", id);
    }
}