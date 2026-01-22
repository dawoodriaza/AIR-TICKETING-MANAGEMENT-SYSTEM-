package com.ga.airticketmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(@NotBlank String email) {}
