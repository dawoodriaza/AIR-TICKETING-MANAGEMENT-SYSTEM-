package com.ga.airticketmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailPasswordResetRequest (@NotBlank String email) {}
