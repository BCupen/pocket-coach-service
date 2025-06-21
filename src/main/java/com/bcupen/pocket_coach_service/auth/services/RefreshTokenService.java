package com.bcupen.pocket_coach_service.auth.services;

import com.bcupen.pocket_coach_service.auth.config.JwtUtils;
import com.bcupen.pocket_coach_service.auth.dtos.RefreshTokenDto;
import com.bcupen.pocket_coach_service.auth.models.RefreshToken;
import com.bcupen.pocket_coach_service.auth.models.User;
import com.bcupen.pocket_coach_service.auth.repositories.RefreshTokenRepository;
import com.bcupen.pocket_coach_service.common.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    public RefreshTokenDto createRefreshToken (User user) {
        String newToken = jwtUtils.generateRefreshToken(user.getEmail());

        LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(jwtUtils.getRefreshTokenExpirySeconds());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(newToken);
        refreshToken.setExpiryDate(expirationDate);

        try {;
            RefreshToken token = refreshTokenRepository.save(refreshToken);
            return new RefreshTokenDto(token.getToken(), jwtUtils.getRefreshTokenExpirySeconds());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create refresh token: " + e.getMessage());
        }
    }

    public void deleteRefreshToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteRefreshTokenByUserEmail(String email) {
        refreshTokenRepository.deleteByUserEmail(email);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }
    }

}
