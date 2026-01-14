package com.ga.airticketmanagement.dto.response;

import com.ga.airticketmanagement.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Long id;
    private Role role;
    private String message;
}
