package com.bcupen.pocket_coach_service.auth.config;

import com.bcupen.pocket_coach_service.common.ApiException;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;

    private final String testSecret = "super-secret-jwt-key-123456789123456789"; // 32+ bytes
    private final int expirationMs = 1000 * 60 * 15; // 15 minutes

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties() {{
            // anonymous subclass with hardcoded values
            try {
                var field = JwtProperties.class.getDeclaredField("secret");
                field.setAccessible(true);
                field.set(this, testSecret);

                field = JwtProperties.class.getDeclaredField("expirationMs");
                field.setAccessible(true);
                field.set(this, expirationMs);

                field = JwtProperties.class.getDeclaredField("refreshExpirationMs");
                field.setAccessible(true);
                field.set(this, expirationMs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }};

        jwtUtils = new JwtUtils(properties);
        jwtUtils.init(); // sets the secret key
    }

    @Test
    void generateToken_shouldReturnValidJwt() {
        String token = jwtUtils.generateToken("john@email.com", null);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length); // basic JWT structure
    }

    @Test
    void extractUsername_shouldReturnCorrectUsername() {
        String token = jwtUtils.generateToken("john@email.com", null);
        String email = jwtUtils.getUserEmailFromToken(token);

        assertEquals("john@email.com", email);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtils.generateToken("john@email.com", null);
        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_shouldThrowOnInvalidToken() {
        String fakeToken = "this.is.not.valid";
        ApiException ex = assertThrows(ApiException.class, () -> jwtUtils.validateToken(fakeToken));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Invalid JWT token"));
    }
}
