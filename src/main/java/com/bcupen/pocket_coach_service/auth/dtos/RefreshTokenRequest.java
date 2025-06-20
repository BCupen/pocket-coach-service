package com.bcupen.pocket_coach_service.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.lang.NonNull;

@Data
public class RefreshTokenRequest {
    @NonNull
    @NotBlank(message = "Refresh token is a required field")
    private String refreshToken;
}
