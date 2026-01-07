package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AccountEmailService {

    @Value("${app.frontend.base-url}")
    private String baseUrl;
    private final EmailService emailService;

    public AccountEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendVerificationEmail(User user, String token) {
        emailService.sendHtmlEmail(
                user.getEmailAddress(),
                "Verify your email",
                "Please verify your email address to gain access to your account." +
                        "<br><br><a href=\""+baseUrl+"/auth/users/verify?token="+ token +"\">Verify Email</a>"
        );
    }
}
