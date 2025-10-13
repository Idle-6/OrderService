package com.sparta.orderservice.auth.infrastructure.util;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-Memory 블랙리스트 저장소
 * - AT 블랙리스트 (key: userId, value: 만료시각)
 */
@Component
public class TokenBlacklistMemoryStore {

    @Getter
    private final Map<Long, Long> atBlacklistExpiry = new ConcurrentHashMap<>();

    public void addBlacklist(Long userId, long expiredMillis) {
        if(userId == null) return;
        atBlacklistExpiry.put(userId, expiredMillis);
    }

    /** userId 확인(지나간 항목은 지연 제거) */
    public boolean isAccessTokenBlacklisted(Long userId) {
        Long exp = atBlacklistExpiry.get(userId);
        if (exp == null) return false;
        long now = System.currentTimeMillis();
        if (exp <= now) { // 만료 시각 지남 : 정리 후 통과
            atBlacklistExpiry.remove(userId); 
            return false;
        }
        return true;
    }

    /** 만료된 AT 블랙리스트 항목 정리 */
    public void sweepExpired() {
        long now = System.currentTimeMillis();
        atBlacklistExpiry.entrySet().removeIf(e -> e.getValue() <= now);
    }
}
