package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.dto.response.PaymentDTO;
import com.ga.airticketmanagement.exception.InformationNotFoundException;
import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.Payment;
import com.ga.airticketmanagement.repository.BookingRepository;
import com.ga.airticketmanagement.repository.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepo paymentRepo;
    private final BookingRepository bookingRepository;
    private final WhatsAppService whatsAppService;

    @Transactional
    public Payment pay(PaymentDTO dto) {
        Booking b = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new InformationNotFoundException("Booking not found"));

//        if (!b.getOtpVerified()) {
//            throw new InformationNotFoundException("OTP not verified. Please verify OTP first.");
//        }

        Payment p = new Payment();
        p.setAmount(dto.getAmount());
        p.setBooking(b);
        p.setPaidAt(LocalDateTime.now());
        p.setStatus("SUCCESS");
        p.setTransactionRef("TXN" + System.currentTimeMillis());

//        b.setStatus("PAID");
        bookingRepository.save(b);
        paymentRepo.save(p);

        String pdfLink = "http://localhost:8080/ticket/pdf/" + b.getId();

//        whatsAppService.send(
//                b.getPhoneNumber(),
//                "Payment Successful!\n" +
//                        "Amount: $" + p.getAmount() + "\n" +
//                        "Reference: " + p.getTransactionRef() + "\n" +
//                        "Download Ticket:\n" + pdfLink,
//                "PAYMENT",
//                b,
//                p,
//                null
//        );

        return p;
    }
}