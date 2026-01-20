package com.ga.airticketmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2)
    private String lastName;

    private String profileImg;
}
