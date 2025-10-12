package com.sparta.orderservice.user.domain.entity;
// @EnableJpaAuditing
// @EntityListeners(AuditingEntityListener.class) 
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @Column(name = "user_id", updatable = false, nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoleEnum role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;   // status → is_active (boolean으로 변경)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist // 엔티티가 DB에 Insert 되기 전에 호출됨
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if(this.createdBy == null) {
            this.createdBy = this.userId; // SEQUENCE라서 pre-insert 시점에 userId 존재
        }
        if(this.updatedBy == null) {
            this.updatedBy = this.userId; // SEQUENCE라서 pre-insert 시점에 userId 존재
        }
    }

    @PreUpdate // 엔티티가 DB에 Update 되기 전에 호출됨
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_by")
    private Long deletedBy;

    public void updateName(String name, Long updatedBy) {
        this.name = name;
        this.updatedBy = updatedBy;
    }

    public void updateAddress(String address, Long updatedBy) {
        this.address = address;
        this.updatedBy = updatedBy;
    }

    public void deactive(Long deletedBy) {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void updatePassword(String password, Long updatedBy) {
        this.password = password;
        this.updatedBy = updatedBy;
    }
}
