package com.ga.airticketmanagement.dto.mapper;

import com.ga.airticketmanagement.dto.response.UserResponse;
import com.ga.airticketmanagement.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toResponse(User user);
}
