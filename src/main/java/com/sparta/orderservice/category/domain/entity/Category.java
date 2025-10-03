package com.sparta.orderservice.category.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private String name;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @Column(nullable = false)
    private Long createdBy;

    private Long updatedBy;

    private Long deletedBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private Category(String name, Long createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    public static Category ofNewCategory(String name, Long createdBy){
        return new Category(name, createdBy);
    }

    public void update(String name, Long updatedBy) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }

    public void delete(Long deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }


}
