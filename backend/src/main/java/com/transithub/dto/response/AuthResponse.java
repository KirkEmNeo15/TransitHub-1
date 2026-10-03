package com.transithub.dto.response;

/** Returned after register and login. The frontend sends the token in the "Authorization: Bearer ..." header. */
public record AuthResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        UserResponse user) {
}
