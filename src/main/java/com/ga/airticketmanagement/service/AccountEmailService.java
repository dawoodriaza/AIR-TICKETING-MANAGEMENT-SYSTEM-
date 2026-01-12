package com.ga.airticketmanagement.service;

import com.ga.airticketmanagement.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class AccountEmailService {

    @Value("${app.frontend.base-url}")
    private String baseUrl;
    private final EmailService emailService;

    @Autowired
    private SpringTemplateEngine templateEngine;
    public AccountEmailService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendVerificationEmail(User user, String token) {
        System.out.println("Sending verification email...");
        String body = buildEmail(baseUrl + "/auth/users/verify?token=" + token, "email/verification");
        emailService.sendHtmlEmail(
                user.getEmailAddress(),
                "Verify Your Email",
                body
        );
    }

    public void sendResetPasswordEmail(User user, String token) {
        System.out.println("Sending reset email...");
        String body = buildEmail(baseUrl + "/auth/users/resetPassword?token=" + token, "email/reset");
        emailService.sendHtmlEmail(
                user.getEmailAddress(),
                "Reset Password",
                body
        );
    }

    private String buildEmail(String link, String templatePath) {
        Context context = new Context();
        context.setVariable("link", link);
        return templateEngine.process(templatePath, context);
    }

}
