package com.transithub.mapper;

import com.transithub.dto.response.UserResponse;
import com.transithub.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive(),
                user.getCreatedAt());
    }
}
