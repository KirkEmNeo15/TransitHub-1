package com.transithub.security;

import com.transithub.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Tells Spring Security how to find a user by email when someone logs in.
 * Spring then compares the typed password with the stored BCrypt hash by itself.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.transithub.entity.User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // our own User class has the same name, so Spring's one is written with its full name
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .disabled(!user.isActive())
                .build();
    }
}
