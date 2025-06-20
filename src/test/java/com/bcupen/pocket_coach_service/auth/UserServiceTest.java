package com.bcupen.pocket_coach_service.auth;


import com.bcupen.pocket_coach_service.auth.config.JwtUtils;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.LoginUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.RefreshTokenRequest;
import com.bcupen.pocket_coach_service.auth.dtos.RefreshTokenResponse;
import com.bcupen.pocket_coach_service.auth.models.User;
import com.bcupen.pocket_coach_service.auth.repositories.UserRepository;
import com.bcupen.pocket_coach_service.auth.services.UserService;
import com.bcupen.pocket_coach_service.common.ApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserService userService;

    @Test
    public void createUser_success() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)){
            CreateUserRequest request = new CreateUserRequest("john", "john@email.com", "password123");

            when(userRepository.existsByEmail("john@email.com")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("hashed");
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
            when(jwtUtils.generateAccessToken("john@email.com")).thenReturn("access-token");
            when(jwtUtils.generateRefreshToken("john@email.com")).thenReturn("refresh-token");
            when(jwtUtils.getAccessTokenExpirySeconds()).thenReturn(900);

            var response = userService.createUser(request);

            assertEquals("john", response.getUsername());
            assertEquals("john@email.com", response.getEmail());
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            assertEquals(900, response.getExpiresIn());

        }
    }

    @Test
    public void createUser_userExists_throwsException() throws Exception{
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)){
            CreateUserRequest request = new CreateUserRequest("john", "john@email.com", "!Password123");

            when(userRepository.existsByEmail("john@email.com")).thenReturn(true);

            ApiException ex = assertThrows(ApiException.class, () -> userService.createUser(request));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, ex.getStatusCode());
        }
    }

    @Test
    public void loginUser_success() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)){
            LoginUserRequest request = new LoginUserRequest("john@email.com", "password123");
            User user = new User();
            user.setEmail("john@email.com");
            user.setPasswordHash("hashed");

            when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
            when(jwtUtils.generateAccessToken("john@email.com")).thenReturn("access-token");
            when(jwtUtils.generateRefreshToken("john@email.com")).thenReturn("refresh-token");
            when(jwtUtils.getAccessTokenExpirySeconds()).thenReturn(900);

            var response = userService.loginUser(request);

            assertEquals("john@email.com", response.getEmail());
            assertEquals("access-token", response.getAccessToken());
            assertEquals("refresh-token", response.getRefreshToken());
            assertEquals(900, response.getExpiresIn());

        }
    }

    @Test
    public void loginUser_invalidCredentials_throwsException() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            LoginUserRequest request = new LoginUserRequest("john@email.com", "wrong");
            User user = new User();
            user.setEmail("john@email.com");
            user.setPasswordHash("hashed");

            when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

            ApiException ex = assertThrows(ApiException.class, () -> userService.loginUser(request));
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        }
    }

    @Test
    public void loginUser_userNotFound_throwsException() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)) {
            LoginUserRequest request = new LoginUserRequest("notfound@email.com", "password");

            when(userRepository.findByEmail("notfound@email.com")).thenReturn(Optional.empty());

            ApiException ex = assertThrows(ApiException.class, () -> userService.loginUser(request));
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());

        }
    }

    @Test
    public void refreshToken_success() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)){
            String refreshToken = "valid-refresh-token";
            String email = "test@example.com";

            when(jwtUtils.getUserEmailFromToken(refreshToken)).thenReturn(email);
            when(jwtUtils.validateToken(refreshToken)).thenReturn(true);
            when(jwtUtils.generateAccessToken(email)).thenReturn("new-access-token");
            when(jwtUtils.generateRefreshToken(email)).thenReturn("new-refresh-token");
            when(jwtUtils.getAccessTokenExpirySeconds()).thenReturn(3600);

            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);
            RefreshTokenResponse response = userService.refreshToken(request);

            assertNotNull(response);
            assertEquals("new-access-token", response.getAccessToken());
            assertEquals("new-refresh-token", response.getRefreshToken());
            assertEquals(3600, response.getExpiresIn());

            verify(jwtUtils).getUserEmailFromToken(refreshToken);
            verify(jwtUtils).validateToken(refreshToken);
        }
    }

    @Test
    public void refreshToken_invalidToken_throwsApiException() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)){
            String refreshToken = "invalid-token";

            when(jwtUtils.getUserEmailFromToken(refreshToken)).thenReturn(null);

            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

            ApiException ex = assertThrows(ApiException.class, () -> userService.refreshToken(request));
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
            assertEquals("Invalid refresh token", ex.getMessage());

            verify(jwtUtils).getUserEmailFromToken(refreshToken);
            verify(jwtUtils, never()).validateToken(refreshToken);
        }
    }

    @Test
    public void refreshToken_emailButInvalidToken_throwsApiException() throws Exception {
        try(AutoCloseable mocks = MockitoAnnotations.openMocks(this)){
            String refreshToken = "bad-token";
            String email = "test@example.com";

            when(jwtUtils.getUserEmailFromToken(refreshToken)).thenReturn(email);
            when(jwtUtils.validateToken(refreshToken)).thenReturn(false);

            RefreshTokenRequest request = new RefreshTokenRequest(refreshToken);

            ApiException ex = assertThrows(ApiException.class, () -> userService.refreshToken(request));
            assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
            assertEquals("Invalid refresh token", ex.getMessage());

            verify(jwtUtils).validateToken(refreshToken);
        }
    }

}
