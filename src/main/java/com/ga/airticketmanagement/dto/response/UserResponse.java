package com.ga.airticketmanagement.dto.response;

import com.ga.airticketmanagement.model.Role;

public record UserResponse(
        Long id,
        String emailAddress,
        Role role,
        boolean active,
        boolean emailVerified
) {}
