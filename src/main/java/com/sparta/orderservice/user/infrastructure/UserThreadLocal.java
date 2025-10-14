package com.sparta.orderservice.user.infrastructure;

public class UserThreadLocal {
    private static final ThreadLocal<Long> userThreadLocal = new ThreadLocal<>();

    private UserThreadLocal() {
        throw new IllegalStateException("Utility class");
    }

    public static Long getUserId() {
        return userThreadLocal.get();
    }

    public static void setUserId(Long userId) {
        userThreadLocal.set(userId);
    }

    public static void removeUserNo() {
        userThreadLocal.remove();
    }
}
