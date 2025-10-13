package com.sparta.orderservice.store.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResStoreDetailDtoV1 {

    private UUID storeId;

    private String categoryName;

    private String name;

    private String bizRegNo;

    private String contact;

    private String address;

    private String description;

    private boolean isPublic = true;

    private Long reviewCount;

    private BigDecimal averageRating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @QueryProjection
    public ResStoreDetailDtoV1(UUID storeId, String categoryName, String name, String bizRegNo, String contact, String address, String description, boolean isPublic, Long reviewCount, BigDecimal averageRating, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.storeId = storeId;
        this.categoryName = categoryName;
        this.name = name;
        this.bizRegNo = bizRegNo;
        this.contact = contact;
        this.address = address;
        this.description = description;
        this.isPublic = isPublic;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}

