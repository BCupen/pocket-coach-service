package com.bcupen.pocket_coach_service.auth;

import com.bcupen.pocket_coach_service.auth.config.JwtUtils;
import com.bcupen.pocket_coach_service.auth.dtos.RefreshTokenDto;
import com.bcupen.pocket_coach_service.auth.models.RefreshToken;
import com.bcupen.pocket_coach_service.auth.models.User;
import com.bcupen.pocket_coach_service.auth.repositories.RefreshTokenRepository;
import com.bcupen.pocket_coach_service.auth.services.RefreshTokenService;
import com.bcupen.pocket_coach_service.common.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private final User mockUser = new User();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser.setEmail("test@example.com");
    }

    @Test
    void createRefreshToken_shouldReturnTokenDto() {
        // Arrange
        String fakeToken = "token-123";
        int expirySeconds = 3600;
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn(fakeToken);
        when(jwtUtils.getRefreshTokenExpirySeconds()).thenReturn(expirySeconds);

        RefreshToken savedToken = new RefreshToken();
        savedToken.setToken(fakeToken);
        savedToken.setUser(mockUser);
        savedToken.setExpiryDate(LocalDateTime.now().plusSeconds(expirySeconds));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedToken);

        // Act
        RefreshTokenDto result = refreshTokenService.createRefreshToken(mockUser);

        // Assert
        assertEquals(fakeToken, result.refreshToken());
        assertEquals(expirySeconds, result.expiresIn());
    }

    @Test
    void createRefreshToken_shouldThrowApiException_onSaveFailure() {
        when(jwtUtils.generateRefreshToken(anyString())).thenReturn("token-123");
        when(jwtUtils.getRefreshTokenExpirySeconds()).thenReturn(3600);
        when(refreshTokenRepository.save(any())).thenThrow(new RuntimeException("DB error"));

        ApiException ex = assertThrows(ApiException.class, () ->
                refreshTokenService.createRefreshToken(mockUser));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("Failed to create refresh token"));
    }

    @Test
    void deleteRefreshToken_shouldCallRepository() {
        refreshTokenService.deleteRefreshToken("some-token");
        verify(refreshTokenRepository, times(1)).deleteByToken("some-token");
    }

    @Test
    void deleteRefreshTokenByUserEmail_shouldCallRepository() {
        refreshTokenService.deleteRefreshTokenByUserEmail("user@email.com");
        verify(refreshTokenRepository, times(1)).deleteByUserEmail("user@email.com");
    }

    @Test
    void findByToken_shouldReturnToken() {
        var mockToken = new RefreshToken();
        when(refreshTokenRepository.findByToken("abc")).thenReturn(Optional.of(mockToken));

        Optional<RefreshToken> found = refreshTokenService.findByToken("abc");

        assertTrue(found.isPresent());
    }

    @Test
    void verifyExpiration_shouldPassForValidToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().plusMinutes(5));

        assertDoesNotThrow(() -> refreshTokenService.verifyExpiration(token));
    }

    @Test
    void verifyExpiration_shouldThrowForExpiredToken() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        ApiException ex = assertThrows(ApiException.class, () ->
                refreshTokenService.verifyExpiration(token));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Refresh token expired", ex.getMessage());
    }
}
