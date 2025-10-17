package com.sparta.orderservice.auth.application.scheduler;

import com.sparta.orderservice.auth.domain.repository.AuthRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Slf4j(topic = "auth Scheduler")
@Service
@RequiredArgsConstructor
public class AuthScheduler {
    private final AuthRepository authRepository;

    @Scheduled(cron = "0 */15 * * * *")
    @Transactional
    public void cleanUpExpiredAuthTokens() {
        int deletedCount = authRepository.deleteAllExpired(LocalDateTime.now());
        if(deletedCount > 0) {
            log.info("[AuthExpiredEmailCleanupScheduler] 만료된 인증번호 " + deletedCount + "개 삭제 완료");
        }
    }
}
