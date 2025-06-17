package com.bcupen.pocket_coach_service.auth.controllers;

import com.bcupen.pocket_coach_service.auth.dtos.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping
    public ResponseEntity<String> getUsers() {
        return ResponseEntity.ok("It works");
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser (@RequestBody CreateUserRequest request) {
        return ResponseEntity.ok("User created");
    }
}


