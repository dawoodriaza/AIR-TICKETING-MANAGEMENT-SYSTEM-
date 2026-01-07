package com.ga.airticketmanagement.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
