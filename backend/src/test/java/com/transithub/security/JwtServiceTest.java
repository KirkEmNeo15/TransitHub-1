package com.transithub.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Plain unit test of the JWT logic: no Spring and no database. */
class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-that-is-long-enough-123456";

    private final JwtService jwtService = new JwtService(SECRET, 60_000);

    @Test
    void aTokenCarriesTheUserId() {
        String token = jwtService.generateToken(42L, "someone@example.com");
        assertEquals(Long.valueOf(42), jwtService.parseUserId(token));
    }

    @Test
    void aTamperedTokenIsRejected() {
        String[] parts = jwtService.generateToken(42L, "someone@example.com").split("\\.");
        // change the first character of the payload: the signature no longer matches
        char first = parts[1].charAt(0);
        String changedPayload = (first == 'e' ? 'f' : 'e') + parts[1].substring(1);
        String tampered = parts[0] + "." + changedPayload + "." + parts[2];

        assertThrows(JwtException.class, () -> jwtService.parseUserId(tampered));
    }

    @Test
    void anExpiredTokenIsRejected() {
        JwtService alreadyExpired = new JwtService(SECRET, -1_000);
        String token = alreadyExpired.generateToken(42L, "someone@example.com");

        assertThrows(JwtException.class, () -> jwtService.parseUserId(token));
    }

    @Test
    void aTokenSignedWithAnotherSecretIsRejected() {
        JwtService other = new JwtService("a-completely-different-secret-value-9876543", 60_000);
        String token = other.generateToken(42L, "someone@example.com");

        assertThrows(JwtException.class, () -> jwtService.parseUserId(token));
    }

    @Test
    void garbageIsRejected() {
        assertThrows(JwtException.class, () -> jwtService.parseUserId("not-a-token"));
    }

    @Test
    void weakOrExampleSecretsAreRefused() {
        assertThrows(IllegalStateException.class, () -> new JwtService("too-short", 60_000));
        assertThrows(IllegalStateException.class,
                () -> new JwtService("replace_this_with_a_long_random_string_at_least_32_chars", 60_000));
    }

    @Test
    void expirationIsReportedInSeconds() {
        assertEquals(60L, jwtService.getExpirationSeconds());
    }
}
