package com.sparta.orderservice.menu.infrastructure.api.gemini.domain.repository;

import com.sparta.orderservice.global.infrastructure.auditing.JpaAuditingConfig;
import com.sparta.orderservice.menu.infrastructure.api.gemini.domain.entity.AiLogEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
@DisplayName("AI 레포지토리")
class AiRepositoryTest {

    @Autowired
    private AiRepository aiRepository;

    @Test
    @DisplayName("AI 로그 저장")
    void testCreate() {

        LocalDateTime now = LocalDateTime.now();
        AiLogEntity aiLogEntity = AiLogEntity.builder()
                .request("request")
                .response("response")
                .createdAt(now)
                .createdBy(1L)
                .build();

        AiLogEntity savedEntity = aiRepository.save(aiLogEntity);
        AiLogEntity retrievedEntity = aiRepository.findById(savedEntity.getId()).orElse(null);

        assertEquals("request", aiLogEntity.getRequest());
        assertEquals("response", aiLogEntity.getResponse());
        assertEquals(now, retrievedEntity.getCreatedAt());
        assertEquals(1L, retrievedEntity.getCreatedBy());
    }
}