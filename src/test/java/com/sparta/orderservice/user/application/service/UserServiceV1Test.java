package com.sparta.orderservice.user.application.service;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import com.sparta.orderservice.user.presentation.advice.UserErrorCode;
import com.sparta.orderservice.user.presentation.advice.UserException;
import com.sparta.orderservice.user.presentation.dto.request.ReqPasswordUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqSignupDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqUserUpdateDtoV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceV1Test {

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtil jwtUtil;

    @InjectMocks
    UserServiceV1 userServiceV1;

    User user;
    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("user@test.com")
                .password("1234")
                .name("user")
                .address("서울 강남구")
                .role(UserRoleEnum.USER)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(user, "userId", 1L);
    }

    @Test
    @DisplayName("USER 가입 성공")
    void signUp_user_success() {
        // given
        ReqSignupDtoV1 dto = new ReqSignupDtoV1();
        dto.setEmail("user@test.com");
        dto.setPassword("1234");
        dto.setName("사용자");
        dto.setAddress("서울 송파구");

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("ENC(1234)");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User saved = userServiceV1.signUp(dto);

        // then
        assertThat(saved.getEmail()).isEqualTo("user@test.com");
        assertThat(saved.getRole()).isEqualTo(UserRoleEnum.USER);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getPassword()).isEqualTo("ENC(1234)");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("OWNER 가입 성공")
    void signUp_owner_success() {
        // given
        ReqSignupDtoV1 dto = new ReqSignupDtoV1();
        dto.setEmail("owner@test.com");
        dto.setPassword("1234");
        dto.setName("owner");
        dto.setAddress("서울 송파구");
        dto.setRole("ROLE_OWNER");

        when(userRepository.findByEmail("owner@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("ENC(1234)");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User saved = userServiceV1.signUp(dto);

        // then
        assertThat(saved.getEmail()).isEqualTo("owner@test.com");
        assertThat(saved.getRole()).isEqualTo(UserRoleEnum.OWNER);
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getPassword()).isEqualTo("ENC(1234)");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 중복")
    void signUp_duplicateEmail() {
        // given
        when(userRepository.findByEmail("dup@test.com")).thenReturn(Optional.of(User.builder().build()));
        ReqSignupDtoV1 dto = new ReqSignupDtoV1();
        dto.setEmail("dup@test.com");
        dto.setPassword("pw");
        dto.setName("name");
        dto.setAddress("address");

        // when / then
        assertThatThrownBy(() -> userServiceV1.signUp(dto))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_DUPLICATE_EMAIL);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("ADMIN 가입 성공")
    void signUp_admin_success() {
        // given
        ReqSignupDtoV1 dto = new ReqSignupDtoV1();
        dto.setEmail("admin@test.com");
        dto.setPassword("1234");
        dto.setName("admin");
        dto.setAddress("서울 송파구");
        dto.setAdmin(true);
        dto.setAdminToken("FcurV51GZOIh6uOGuhyg6dG3odIYYsRM0");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("1234")).thenReturn("ENC(1234)");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User saved = userServiceV1.signUp(dto);

        // then
        assertThat(saved.getRole()).isEqualTo(UserRoleEnum.ADMIN);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("ADMIN 가입 실패 - 토큰 불일치")
    void signUp_admin_wrongToken() {
        // given
        ReqSignupDtoV1 dto = new ReqSignupDtoV1();
        dto.setEmail("admin@test.com");
        dto.setPassword("1234");
        dto.setName("admin");
        dto.setAddress("서울 송파구");
        dto.setAdmin(true);
        dto.setAdminToken("WRONG");

        // when / then
        assertThatThrownBy(() -> userServiceV1.signUp(dto))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_INVALID_ADMIN_TOKEN);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("이름/주소 변경")
    void updateUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        ReqUserUpdateDtoV1 dto = new ReqUserUpdateDtoV1();
        dto.setName("newName");
        dto.setAddress("newAddr");

        // when
        User updated = userServiceV1.updateUser(1L, dto);

        // then
        assertThat(updated.getName()).isEqualTo("newName");
        assertThat(updated.getAddress()).isEqualTo("newAddr");
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword_success() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("RAW(1234)", "1234")).thenReturn(true);
        when(passwordEncoder.encode("newPw")).thenReturn("ENC(newPw)");

        ReqPasswordUpdateDtoV1 dto = new ReqPasswordUpdateDtoV1();
        dto.setCurrentPassword("RAW(1234)");
        dto.setNewPassword("newPw");

        // when
        userServiceV1.updaterPassword(1L, dto);

        // then
        assertThat(user.getPassword()).isEqualTo("ENC(newPw)");
    }

    @Test
    @DisplayName("비밀번호 검증 실패")
    void validatePassword_mismatch() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WRONG", "1234")).thenReturn(false);

        ReqPasswordUpdateDtoV1 dto = new ReqPasswordUpdateDtoV1();
        dto.setCurrentPassword("RAW(1234)");
        dto.setNewPassword("newPw");

        // when / then
        assertThatThrownBy(() -> userServiceV1.validatePassword(1L, "WRONG"))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_INVALID_PASSWORD);
    }

    @Test
    @DisplayName("사용자 탈퇴 : 사용자 비활성 + refresh 쿠키 만료 호출")
    void deleteUser() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        userServiceV1.deleteUser(response, 1L);

        // then
        assertThat(user.isActive()).isFalse();
        verify(jwtUtil, times(1)).expireRefreshCookie(response);
    }

    @Test
    @DisplayName("회원 조회 실패")
    void findById_fail() {
        // given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> userServiceV1.findById(999L))
                .isInstanceOf(UserException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }
}