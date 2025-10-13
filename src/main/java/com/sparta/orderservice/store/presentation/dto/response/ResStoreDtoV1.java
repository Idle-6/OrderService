package com.sparta.orderservice.store.presentation.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ResStoreDtoV1 {

    private UUID storeId;

    private String categoryName;

    private String name;

    private String contact;

    private String address;

    private String description;

    private Long reviewCount;

    private BigDecimal averageRating;


    @QueryProjection
    public ResStoreDtoV1(UUID storeId, String categoryName, String name, String contact, String address, String description, Long reviewCount, BigDecimal averageRating) {
        this.storeId = storeId;
        this.categoryName = categoryName;
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.description = description;
        this.reviewCount = reviewCount;
        this.averageRating = averageRating;
    }
}
