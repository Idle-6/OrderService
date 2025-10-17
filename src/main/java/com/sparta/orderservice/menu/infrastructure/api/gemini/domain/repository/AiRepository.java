package com.sparta.orderservice.menu.infrastructure.api.gemini.domain.repository;

import com.sparta.orderservice.menu.infrastructure.api.gemini.domain.entity.AiLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiRepository extends JpaRepository<AiLogEntity, UUID> {
}
