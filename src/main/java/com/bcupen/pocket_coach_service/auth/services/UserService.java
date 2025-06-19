package com.bcupen.pocket_coach_service.auth.services;

import com.bcupen.pocket_coach_service.auth.config.JwtUtils;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserResponse;
import com.bcupen.pocket_coach_service.auth.dtos.LoginUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.LoginUserResponse;
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

@Service
@RequiredArgsConstructor
public class UserService {
    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public CreateUserResponse createUser(CreateUserRequest request){
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername((request.getUsername()));
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDate.now());
        user.setUpdatedAt(LocalDate.now());

        try {
            // Optional: check if user with email exists before save
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"User with email " + user.getEmail() + " already exists.");
            }
            User newUser = userRepository.save(user);
            String accessToken = jwtUtils.generateAccessToken(newUser.getEmail());
            String refreshToken = jwtUtils.generateRefreshToken(newUser.getEmail());
            return CreateUserResponse.builder()
                    .email(newUser.getEmail())
                    .username((newUser.getUsername()))
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
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

        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return LoginUserResponse.builder()
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtils.getAccessTokenExpirySeconds()) // Convert ms to seconds
                .build();

    }
}
