package com.sparta.orderservice.user.application.service;

import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import com.sparta.orderservice.user.presentation.dto.request.ReqPasswordUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqSignupDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqUserUpdateDtoV1;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceV1 {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
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
        return null;
    }

    public void updaterPassword(Long userId, ReqPasswordUpdateDtoV1 requestDto) {
        User user = validatePassword(userId, requestDto.getCurrentPassword());
        String password = passwordEncoder.encode(requestDto.getNewPassword());

        user.updatePassword(password);
    }

    private User validatePassword(Long userId, @NotBlank String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("회원을 찾을 수 없습니다."));

        if(!passwordEncoder.matches(password, user.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        if(!user.isActive()){
            throw new IllegalStateException("이미 탈퇴한 사용자입니다.");
        }

        user.delete(user.getUserId());
    }
}
