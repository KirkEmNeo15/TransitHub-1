package com.transithub.security;

import com.transithub.entity.enums.Role;

/**
 * The logged-in user for the current request.
 * Controllers receive it with @AuthenticationPrincipal.
 */
public record AuthenticatedUser(Long id, String email, Role role) {
}
