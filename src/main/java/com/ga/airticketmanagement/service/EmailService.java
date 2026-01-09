package com.ga.airticketmanagement.service;

public interface EmailService {
    void sendTxtEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String body);
}
