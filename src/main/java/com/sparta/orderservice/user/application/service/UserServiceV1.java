package com.sparta.orderservice.user.application.service;

import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.infrastructure.util.TokenBlacklistMemoryStore;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import com.sparta.orderservice.user.presentation.dto.request.ReqPasswordUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqSignupDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqUserUpdateDtoV1;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistMemoryStore tokenBlacklistMemoryStore;

    private final String ADMIN_TOKEN = "FcurV51GZOIh6uOGuhyg6dG3odIYYsRM0";

    public User signUp(ReqSignupDtoV1 requestDto) {
        String email = requestDto.getEmail();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인(email)
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 사용자 ROLE 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 일치하지 않습니다.");
            }
            role = UserRoleEnum.ADMIN;
        }else if(!requestDto.getRole().isEmpty()) {
            role = UserRoleEnum.OWNER;
        }

        // 사용자 등록
        User user = User.builder()
                .email(email)
                .password(password)
                .name(requestDto.getName())
                .address(requestDto.getAddress())
                .role(role)
                .isActive(true)
                .build();

        return userRepository.save(user);
    }


    public User updateUser(Long userId, ReqUserUpdateDtoV1 requestDto) {
        User user = findById(userId);

        if(requestDto.getName() != null) user.updateName(requestDto.getName(), userId);
        if(requestDto.getAddress() != null) user.updateAddress(requestDto.getAddress(), userId);

        return user;
    }

    public void updaterPassword(Long userId, ReqPasswordUpdateDtoV1 requestDto) {
        User user = validatePassword(userId, requestDto.getCurrentPassword());
        String password = passwordEncoder.encode(requestDto.getNewPassword());

        user.updatePassword(password, userId);
    }

    @Transactional(readOnly = true)
    public User validatePassword(Long userId, @NotBlank String password) {
        User user = findById(userId);

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public void deleteUser(HttpServletRequest request, HttpServletResponse response, Long userId) {
        User user = findById(userId);

        if(!user.isActive()){
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        user.deactive(user.getUserId());
        jwtUtil.expireRefreshCookie(response);
    }

    @Transactional(readOnly = true)
    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
}
