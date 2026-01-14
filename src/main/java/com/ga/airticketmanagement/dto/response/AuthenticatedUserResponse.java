package com.ga.airticketmanagement.dto.response;

import com.ga.airticketmanagement.model.Role;

public record AuthenticatedUserResponse (
        Long id,
        Role role
){}
