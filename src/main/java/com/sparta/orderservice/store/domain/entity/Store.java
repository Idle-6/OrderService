package com.sparta.orderservice.store.domain.entity;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String bizRegNo;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private String address;

    @Column(columnDefinition = "text")
    private String description;

    private boolean isPublic;

    private Long reviewCount;

    @Column(precision = 3, scale = 2)
    private BigDecimal averageRating;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private User createdBy;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

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

    private Store(String name, String bizRegNo, String contact, String address, String description, boolean isPublic, Category category, User createdBy) {
        this.name = name;
        this.bizRegNo = bizRegNo;
        this.contact = contact;
        this.address = address;
        this.description = description;
        this.isPublic = isPublic;
        this.category = category;
        this.createdBy = createdBy;
    }

    public static Store ofNewStore(String name, String bizRegNo, String contact, String address, String description, boolean isPublic, Category category, User createdBy) {
        return new Store(name, bizRegNo, contact, address, description, isPublic, category, createdBy);
    }

    public void update(ReqStoreUpdateDtoV1 request, Category newCategory, Long updatedBy) {
        if (newCategory != null) this.category = newCategory;
        if (request.getName() != null) this.name = request.getName();
        if (request.getBizRegNo() != null) this.bizRegNo = request.getBizRegNo();
        if (request.getContact() != null) this.contact = request.getContact();
        if (request.getAddress() != null) this.address = request.getAddress();
        if (request.getDescription()!= null) this.description= request.getDescription();
        this.isPublic = request.isPublic();
        this.updatedBy = updatedBy;
    }


    public void delete(Long deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

}
