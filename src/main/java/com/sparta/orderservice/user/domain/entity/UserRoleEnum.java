package com.sparta.orderservice.user.domain.entity;

public enum UserRoleEnum {
    USER(Authority.USER),  // 사용자 권한
    OWNER(Authority.OWNER), // 점주 권한
    ADMIN(Authority.ADMIN); // 관리자 권한

    private final String authority; // 실제 권한 문자열("ROLE_USER" 등)

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
