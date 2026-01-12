package com.ga.airticketmanagement.dto.request;

public record PasswordResetRequest(
        String oldPassword,
        String newPassword,
        String newPasswordConfirmation
) {}
