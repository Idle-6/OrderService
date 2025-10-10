package com.sparta.orderservice.user.application.service;

import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import com.sparta.orderservice.user.domain.repository.UserRepository;
import com.sparta.orderservice.user.presentation.dto.request.ReqPasswordUpdateDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqSignupDtoV1;
import com.sparta.orderservice.user.presentation.dto.request.ReqUserUpdateDtoV1;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public User updaterPassword(Long userId, ReqPasswordUpdateDtoV1 requestDto) {
        return null;
    }
}
