package com.sparta.orderservice.user.domain.repository;

import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager em;

    private User persistUser(String email) {
        User user = User.builder()
                .email(email)
                .password("1234")
                .name("관리자")
                .address("서울 강남구")
                .role(UserRoleEnum.ADMIN)
                .isActive(true)
                .build();

        em.persistAndFlush(user);
        em.clear();
        return user;
    }

    @Test
    @DisplayName("이메일 조회")
    void findByEmail_found() {
        // given
        String email = "admin@test.com";
        User saved = persistUser(email);

        // when
        Optional<User> found = userRepository.findByEmail(email);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(saved.getUserId());
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("이메일 조회 실패")
    void findByEmail_notFound() {
        // when
        Optional<User> found = userRepository.findByEmail("none@test.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("토큰 만료 시간 수정")
    void updateTokenExpiredAtById() {
        // given
        User saved = persistUser("exp@test.com");
        Long userId = saved.getUserId();
        long newExp = System.currentTimeMillis();

        // when
        int updatedRows = userRepository.updateTokenExpiredAtById(userId, newExp);

        // then
        assertThat(updatedRows).isEqualTo(1);

        // 영속성 컨텍스트 초기화 후 값 확인
        em.clear();
        User refreshed = em.find(User.class, userId);
        assertThat(refreshed.getTokenExpiredAt()).isEqualTo(newExp);
    }
}