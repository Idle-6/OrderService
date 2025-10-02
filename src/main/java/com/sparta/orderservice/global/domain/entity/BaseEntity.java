package com.sparta.orderservice.global.domain.entity;

import com.sparta.orderservice.global.infrastructure.config.auditing.AuditorAwareImpl;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;

    private Long deletedBy;

    @PreRemove
    public void delete() {
        AuditorAware<Long> auditorAware = new AuditorAwareImpl();

        deletedAt = LocalDateTime.now();
        deletedBy = auditorAware.getCurrentAuditor().orElse(Long.MIN_VALUE);
    }
}
