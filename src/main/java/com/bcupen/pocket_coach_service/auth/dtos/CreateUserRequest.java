package com.bcupen.pocket_coach_service.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;

    private String email;

    private String password;
}
