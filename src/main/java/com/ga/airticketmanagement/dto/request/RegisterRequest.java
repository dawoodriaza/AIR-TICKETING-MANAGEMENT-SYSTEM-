package com.ga.airticketmanagement.dto.request;
import com.ga.airticketmanagement.model.UserProfile;
import com.ga.airticketmanagement.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @Email @NotBlank
        String emailAddress,
        @NotBlank @Size(min = 8)
        String password,
        Role role,
        UserProfile userProfile
) {}