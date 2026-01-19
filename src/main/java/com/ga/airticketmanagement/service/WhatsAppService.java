package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.Booking;
import com.ga.airticketmanagement.model.Payment;
import com.ga.airticketmanagement.model.WhatsAppLog;
import com.ga.airticketmanagement.repository.WhatsAppRepo;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    @Value("${twilio.whatsappFrom}")
    private String from;

    private final WhatsAppRepo repo;

    public WhatsAppLog send(
            String phone,
            String message,
            String type,
            Booking booking,
            Payment payment,
            String otp
    ) {
        WhatsAppLog wlog = new WhatsAppLog();
        wlog.setPhoneNumber(phone);
        wlog.setMessage(message);
        wlog.setType(type);
        wlog.setBooking(booking);
        wlog.setPayment(payment);
        wlog.setOtpCode(otp);

        try {
            log.info("Sending WhatsApp to: {}\nMessage: {}\nFrom: {}", phone, message, from);

            Message.creator(
                    new PhoneNumber("whatsapp:" + phone),
                    new PhoneNumber(from),
                    message
            ).create();

            wlog.setStatus("SENT");
            log.info("WhatsApp sent successfully");
        } catch (Exception e) {
            wlog.setStatus("FAILED");
            wlog.setMessage(message + " | ERROR: " + e.getMessage());
            log.error("WhatsApp failed: {}", e.getMessage());
            e.printStackTrace();
        }

        return repo.save(wlog);
    }
}