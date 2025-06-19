package com.bcupen.pocket_coach_service.auth.services;

import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserResponse;
import com.bcupen.pocket_coach_service.auth.models.User;
import com.bcupen.pocket_coach_service.auth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.cglib.core.Local;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserService {
    private final  UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                throw new ServiceException("User with email " + user.getEmail() + " already exists.");
            }
            User newUser = userRepository.save(user);
            return CreateUserResponse.builder()
                    .email(newUser.getEmail())
                    .username((newUser.getUsername()))
                    .build();
        } catch (Exception e) {
            throw new ServiceException("Error saving new user: " + e.getMessage(), e);
        }
    }


}
