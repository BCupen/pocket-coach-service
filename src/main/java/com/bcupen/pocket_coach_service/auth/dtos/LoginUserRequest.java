package com.bcupen.pocket_coach_service.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserRequest {
    @NonNull
    private String email;
    @NonNull
    private String password;
}
