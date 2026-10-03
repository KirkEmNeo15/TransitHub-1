package com.transithub.repository;

import com.transithub.entity.User;
import com.transithub.entity.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Database access for users.
 * We only write the method SIGNATURES. Spring Data reads the method name
 * (findByEmail -> "WHERE email = ?") and creates the implementation for us.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    // For the admin user table (search by name or email, with pagination)
    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable);
}
