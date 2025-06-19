package com.bcupen.pocket_coach_service.auth.dtos;

import lombok.*;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponse {
    @NonNull
    private String email;

    @NonNull
    private String accessToken;

    @NonNull
    private String refreshToken;

    @Nullable
    private Integer expiresIn;
}
