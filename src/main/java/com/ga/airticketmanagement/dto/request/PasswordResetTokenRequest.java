package com.ga.airticketmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordResetTokenRequest(
        @NotBlank
        String token,
        @NotBlank @Size(min = 8)
        String newPassword,
        @NotBlank @Size(min = 8)
        String newPasswordConfirmation
) {}
