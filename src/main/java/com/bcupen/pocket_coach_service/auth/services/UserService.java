package com.bcupen.pocket_coach_service.auth.services;

import com.bcupen.pocket_coach_service.auth.config.JwtUtils;
import com.bcupen.pocket_coach_service.auth.dtos.*;
import com.bcupen.pocket_coach_service.auth.models.User;
import com.bcupen.pocket_coach_service.auth.repositories.UserRepository;
import com.bcupen.pocket_coach_service.common.ApiException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {
    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public CreateUserResponse createUser(CreateUserRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername((request.getUsername()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        try {
            // Optional: check if user with email exists before save
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"User with email " + user.getEmail() + " already exists.");
            }
            User newUser = userRepository.save(user);
            String accessToken = jwtUtils.generateAccessToken(newUser.getEmail());
            RefreshTokenDto refreshToken = refreshTokenService.createRefreshToken(newUser);
            return CreateUserResponse.builder()
                    .email(newUser.getEmail())
                    .username((newUser.getUsername()))
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.refreshToken())
                    .expiresIn(jwtUtils.getAccessTokenExpirySeconds()) // Convert ms to seconds
                    .build();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    public LoginUserResponse loginUser(@Valid LoginUserRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        refreshTokenService.deleteRefreshTokenByUserEmail(user.getEmail());

        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        RefreshTokenDto refreshToken = refreshTokenService.createRefreshToken(user);

        return LoginUserResponse.builder()
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken.refreshToken())
                .expiresIn(jwtUtils.getAccessTokenExpirySeconds()) // Convert ms to seconds
                .build();

    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String email = jwtUtils.getUserEmailFromToken(request.getRefreshToken());
        if (email == null || !jwtUtils.validateToken(request.getRefreshToken())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));

        refreshTokenService.deleteRefreshTokenByUserEmail(user.getEmail());

        String newAccessToken = jwtUtils.generateAccessToken(email);
        RefreshTokenDto newRefreshToken = refreshTokenService.createRefreshToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken.refreshToken())
                .expiresIn(jwtUtils.getAccessTokenExpirySeconds()) // Convert ms to seconds
                .build();
    }
}
