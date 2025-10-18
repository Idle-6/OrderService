package com.sparta.orderservice.user.domain.repository;

import com.sparta.orderservice.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.tokenExpiredAt = :exp where u.userId = :userId")
    int updateTokenExpiredAtById(@Param("userId") Long userId, @Param("exp") Long tokenExpiredAt);
}
