package com.bcupen.pocket_coach_service.auth.repositories;

import com.bcupen.pocket_coach_service.auth.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail (String email);
    Optional<User> findByUsername (String username);
}
