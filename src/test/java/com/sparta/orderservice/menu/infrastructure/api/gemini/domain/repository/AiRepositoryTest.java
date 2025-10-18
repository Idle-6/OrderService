package com.sparta.orderservice.menu.infrastructure.api.gemini.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.auth.infrastructure.util.JwtProperties;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.global.infrastructure.config.SecurityConfig;
import com.sparta.orderservice.global.infrastructure.config.auditing.JpaAuditingConfig;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsServiceImpl;
import com.sparta.orderservice.menu.infrastructure.api.gemini.domain.entity.AiLogEntity;
import com.sparta.orderservice.payment.domain.repository.impl.CustomPaymentRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        },
        excludeAutoConfiguration = CustomPaymentRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JwtUtil.class, JwtProperties.class, UserDetailsServiceImpl.class, JpaAuditingConfig.class})
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(JpaAuditingConfig.class)
@DisplayName("AI 레포지토리")
class AiRepositoryTest {

    @MockitoBean
    private JPAQueryFactory jpaQueryFactory;

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