package com.ga.airticketmanagement.controller;

import com.ga.airticketmanagement.dto.request.PaymentRequest;
import com.ga.airticketmanagement.dto.response.ListResponse;
import com.ga.airticketmanagement.dto.response.PaymentResponse;
import com.ga.airticketmanagement.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(@Valid @RequestBody PaymentRequest request) {
        log.debug("POST /api/payments - Creating payment");
        return paymentService.createPayment(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse<PaymentResponse> getAllPayments(Pageable pageable) {
        log.debug("GET /api/payments - Fetching all payments");
        return paymentService.getAllPayments(pageable);
    }

    @GetMapping("/my-payments")
    public ListResponse<PaymentResponse> getCurrentUserPayments(Pageable pageable) {
        return paymentService.getCurrentUserPayments(pageable);
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse<PaymentResponse> getPaymentsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        log.debug("GET /api/payments/users/{}", userId);
        return paymentService.getPaymentsByUserId(userId, pageable);
    }



    @GetMapping("/bookings/{bookingId}")
    public ListResponse<PaymentResponse> getPaymentsByBookingId(
            @PathVariable Long bookingId,
            Pageable pageable) {
        log.debug("GET /api/payments/bookings/{}", bookingId);
        return paymentService.getPaymentsByBookingId(bookingId, pageable);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ListResponse<PaymentResponse> getPaymentsByStatus(
            @PathVariable String status,
            Pageable pageable) {
        log.debug("GET /api/payments/status/{}", status);
        return paymentService.getPaymentsByStatus(status, pageable);
    }



    @GetMapping("/{id}")
    public PaymentResponse getPaymentById(@PathVariable Long id) {
        log.debug("GET /api/payments/{}", id);
        return paymentService.getPaymentById(id);
    }

    @GetMapping("/transaction/{transactionRef}")
    public PaymentResponse getPaymentByTransactionRef(@PathVariable String transactionRef) {
        log.debug("GET /api/payments/transaction/{}", transactionRef);
        return paymentService.getPaymentByTransactionRef(transactionRef);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse updatePayment(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequest request) {
        log.debug("PUT /api/payments/{}", id);
        return paymentService.updatePayment(id, request);
    }

    @PostMapping("/{id}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponse refundPayment(@PathVariable Long id) {
        log.debug("POST /api/payments/{}/refund", id);
        return paymentService.refundPayment(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        log.debug("DELETE /api/payments/{}", id);
        paymentService.deletePayment(id);
    }
}