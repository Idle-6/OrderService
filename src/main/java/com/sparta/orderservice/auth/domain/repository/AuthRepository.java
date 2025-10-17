package com.sparta.orderservice.auth.domain.repository;

import com.sparta.orderservice.auth.domain.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


public interface AuthRepository extends JpaRepository<Auth, UUID> {
    Optional<Auth> findTopByTokenHashAndConsumedAtIsNull(String tokenHash);

    void deleteByEmail(String email);

    @Modifying
    @Query("delete from Auth a where a.expiresAt < :now")
    int deleteAllExpired(@Param("now") LocalDateTime now);
}
