package com.bcupen.pocket_coach_service.auth.repositories;

import com.bcupen.pocket_coach_service.auth.models.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void deleteByToken(String token);

    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user.email = :email")
    void deleteByUserEmail(@Param("email") String email);

    Optional<RefreshToken> findByToken(String token);
}
