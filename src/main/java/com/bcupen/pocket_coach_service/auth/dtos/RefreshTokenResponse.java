package com.bcupen.pocket_coach_service.auth.dtos;

import lombok.*;
import org.springframework.lang.NonNull;

@Data
@Builder
@AllArgsConstructor
public class RefreshTokenResponse {
    @NonNull
    private String accessToken;

    @NonNull
    private String refreshToken;

    private int expiresIn;

}
