package com.ga.airticketmanagement.dto.request;

public record PasswordResetTokenRequest(
        String token,
        String newPassword,
        String newPasswordConfirmation
) {}
