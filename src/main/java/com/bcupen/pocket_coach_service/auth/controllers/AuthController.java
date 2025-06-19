package com.bcupen.pocket_coach_service.auth.controllers;

import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.CreateUserResponse;
import com.bcupen.pocket_coach_service.auth.dtos.LoginUserRequest;
import com.bcupen.pocket_coach_service.auth.dtos.LoginUserResponse;
import com.bcupen.pocket_coach_service.auth.services.UserService;
import com.bcupen.pocket_coach_service.common.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<String> getUsers() {
        return ResponseEntity.ok("It works");
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CreateUserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        CreateUserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginUserResponse>> loginUser(
            @Valid @RequestBody LoginUserRequest request) {
        LoginUserResponse response = userService.loginUser(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Login successful", response));
    }

}


