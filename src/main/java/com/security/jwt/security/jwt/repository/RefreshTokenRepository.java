package com.security.jwt.security.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository  extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUsername(String username);

    boolean existsByUsernameAndToken(String username, String token);
}