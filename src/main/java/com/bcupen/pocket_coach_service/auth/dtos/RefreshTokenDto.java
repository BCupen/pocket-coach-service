package com.bcupen.pocket_coach_service.auth.dtos;

public record RefreshTokenDto(
        String refreshToken,
        int expiresIn
) {
}
