package com.sparta.orderservice.menu.domain.repository;

import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MenuRepository extends JpaRepository<MenuEntity, UUID> {
    Optional<MenuEntity> findByName(String name);
}
