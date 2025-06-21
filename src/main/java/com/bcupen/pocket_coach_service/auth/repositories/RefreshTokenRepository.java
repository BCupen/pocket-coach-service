package com.bcupen.pocket_coach_service.auth.repositories;

import com.bcupen.pocket_coach_service.auth.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String token);
    void deleteByUserEmail(String email);

    Optional<RefreshToken> findByToken(String token);
}
