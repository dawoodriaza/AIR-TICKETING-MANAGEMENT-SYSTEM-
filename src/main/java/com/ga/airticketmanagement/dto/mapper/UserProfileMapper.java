package com.ga.airticketmanagement.dto.mapper;

import com.ga.airticketmanagement.dto.request.UserProfileRequest;
import com.ga.airticketmanagement.dto.response.UserProfileResponse;
import com.ga.airticketmanagement.model.UserProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    UserProfileResponse toResponse(UserProfile userProfile);
    UserProfile toEntity(UserProfileRequest userProfileRequest);
}
