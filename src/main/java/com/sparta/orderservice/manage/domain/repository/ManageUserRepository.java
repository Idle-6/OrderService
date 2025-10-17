package com.sparta.orderservice.manage.domain.repository;

import com.sparta.orderservice.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ManageUserRepository extends JpaRepository<User, Long> {
    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name, String email, Pageable pageable
    );

    Optional<User> findByUserId(Long userId);

    // 활성화 (soft-delete 해제)
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
       update User u
          set u.isActive = true,
              u.updatedAt = :updatedAt,
              u.updatedBy = :updatedBy
        where u.userId = :userId
       """)
    int activate(
            @Param("userId") Long userId,
            @Param("updatedAt") LocalDateTime updatedAt,
            @Param("updatedBy") Long updatedBy
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
       update User u
          set u.isActive = false,
              u.updatedAt = :updatedAt,
              u.updatedBy = :updatedBy
        where u.userId = :userId
       """)
    int deactivate(
            @Param("userId") Long userId,
            @Param("updatedAt") LocalDateTime updatedAt,
            @Param("updatedBy") Long updatedBy
    );


}
