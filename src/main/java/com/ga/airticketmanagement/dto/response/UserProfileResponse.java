package com.ga.airticketmanagement.dto.response;

public record UserProfileResponse (
        Long id,
        String firstName,
        String lastName,
        String profileImg
){}