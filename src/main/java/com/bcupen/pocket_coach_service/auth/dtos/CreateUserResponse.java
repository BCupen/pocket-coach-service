package com.bcupen.pocket_coach_service.auth.dtos;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.UUID;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserResponse {
    @NonNull
    private String email;

    @NonNull
    private String username;

    @Nullable
    private String accessToken;

    @Nullable
    private String refreshToken;

    @Nullable
    private Integer expiresIn;
}
