package com.sparta.orderservice.menu.domain.repository;

import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<MenuEntity, UUID> {
    Optional<MenuEntity> findByName(String name);

    Page<MenuEntity> findAllByStoreIdAndNameLike(UUID storeId, String search, Pageable pageable);

    Page<MenuEntity> findAllByStoreIdAndIsPublicTrueAndNameLike(UUID storeId, String search, Pageable pageable);
}
