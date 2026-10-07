package com.transithub.config;

import com.transithub.entity.User;
import com.transithub.entity.enums.Role;
import com.transithub.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates one demo admin and one demo user when the application starts (only if they do not exist).
 * The passwords are NOT in the source code: they come from DEMO_ADMIN_PASSWORD and
 * DEMO_USER_PASSWORD in your .env file. If a password is not set, that account is skipped.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String userEmail;
    private final String userPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.demo.admin-email}") String adminEmail,
                      @Value("${app.demo.admin-password}") String adminPassword,
                      @Value("${app.demo.user-email}") String userEmail,
                      @Value("${app.demo.user-password}") String userPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Override
    public void run(String... args) {
        createIfMissing("Demo Admin", adminEmail, adminPassword, Role.ADMIN);
        createIfMissing("Demo User", userEmail, userPassword, Role.USER);
    }

    private void createIfMissing(String fullName, String email, String password, Role role) {
        if (password == null || password.length() < 8) {
            log.info("Demo {} account skipped: set a password of at least 8 characters in your .env file", role);
            return;
        }
        String normalizedEmail = email.trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            return;
        }
        userRepository.save(new User(fullName, normalizedEmail, passwordEncoder.encode(password), role));
        log.info("Demo {} account created: {}", role, normalizedEmail);
    }
}
