package com.sparta.orderservice.manage.domain.repository;

import com.sparta.orderservice.store.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface ManageStoreRepository extends JpaRepository<Store, UUID> {

    Page<Store> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<Store> findByStoreId(UUID storeId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Store s
              set s.isPublic = false,
                  s.updatedAt = :updatedAt,
                  s.updatedBy = :updatedBy
            where s.storeId = :storeId
           """)
    int deactivateStore(
            @Param("storeId") UUID storeId,
            @Param("updatedAt") LocalDateTime updatedAt,
            @Param("updatedBy") Long updatedBy
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update Store s
              set s.isPublic = true,
                  s.updatedAt = :updatedAt,
                  s.updatedBy = :updatedBy
            where s.storeId = :storeId
           """)
    int activateStore(
            @Param("storeId") UUID storeId,
            @Param("updatedAt") LocalDateTime updatedAt,
            @Param("updatedBy") Long updatedBy
    );
}
