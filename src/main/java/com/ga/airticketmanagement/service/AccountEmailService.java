package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
public class AccountEmailService {

    @Value("${app.backend.base-url}")
    private String baseUrl;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    private final EmailService emailService;

    @Autowired
    private SpringTemplateEngine templateEngine;
    public AccountEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendVerificationEmail(User user, String token) {
        log.info("Sending verification email...");
        String body = buildEmail(baseUrl + "/auth/users/verify?token=" + token, "email/verification");
        emailService.sendHtmlEmail(
                user.getEmailAddress(),
                "Verify Your Email",
                body
        );
        log.info("Verification Email Sent.");
    }

    public void sendResetPasswordEmail(User user, String token) {
        log.info("Sending reset email...");
        String body = buildEmail(frontendBaseUrl + "/reset-password?token=" + token, "email/reset");
        emailService.sendHtmlEmail(
                user.getEmailAddress(),
                "Reset Password",
                body
        );
        log.info("Reset Email Sent.");
    }

    private String buildEmail(String link, String templatePath) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process(templatePath, context);
    }

}
