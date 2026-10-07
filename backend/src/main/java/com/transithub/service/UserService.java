package com.transithub.service;

import com.transithub.dto.PageResponse;
import com.transithub.dto.response.UserResponse;
import com.transithub.entity.User;
import com.transithub.entity.enums.Role;
import com.transithub.exception.InvalidRequestException;
import com.transithub.exception.ResourceNotFoundException;
import com.transithub.mapper.UserMapper;
import com.transithub.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** User management for admins. */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(String text, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 100), Sort.by("fullName"));
        String keyword = (text == null) ? "" : text.trim();

        Page<User> result = userRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);

        List<UserResponse> items = result.getContent().stream().map(userMapper::toResponse).toList();
        return new PageResponse<>(items, result.getNumber(), result.getSize(),
                result.getTotalElements(), result.getTotalPages());
    }

    /** Activates or deactivates an account. Admins cannot deactivate themselves (they would lock themselves out). */
    @Transactional
    public UserResponse setActive(Long adminId, Long userId, boolean active) {
        if (adminId.equals(userId) && !active) {
            throw new InvalidRequestException("You cannot deactivate your own account");
        }
        User user = find(userId);
        user.setActive(active);
        return userMapper.toResponse(user);
    }

    /** Changes a role. Admins cannot remove their own admin role. */
    @Transactional
    public UserResponse changeRole(Long adminId, Long userId, Role role) {
        if (adminId.equals(userId) && role != Role.ADMIN) {
            throw new InvalidRequestException("You cannot remove your own admin role");
        }
        User user = find(userId);
        user.setRole(role);
        return userMapper.toResponse(user);
    }

    private User find(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
    }
}
