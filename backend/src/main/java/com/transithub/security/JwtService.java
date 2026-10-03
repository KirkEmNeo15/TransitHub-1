package com.transithub.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Creates and checks JWT login tokens.
 * A JWT is a signed piece of text. Only someone who knows our secret can create a valid one,
 * and any change to it makes the signature check fail.
 * The token only says WHO the user is (their id). Their role is always read from the database.
 */
@Service
public class JwtService {

    private static final int MIN_SECRET_LENGTH = 32;

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs) {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "JWT_SECRET must be at least " + MIN_SECRET_LENGTH + " characters. Set it in your .env file.");
        }
        if (secret.startsWith("replace_this")) {
            throw new IllegalStateException(
                    "JWT_SECRET still has the example value. Put your own long random string in your .env file.");
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Checks the signature and the expiry date, and returns the user id.
     *
     * @throws JwtException             if the token is invalid, tampered with or expired
     * @throws IllegalArgumentException if the token is empty or the id is not a number
     */
    public Long parseUserId(String token) {
        String subject = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.valueOf(subject);
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }
}
