package com.sparta.orderservice.store.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResStoreDtoV1 {

    private UUID storeId;

    private UUID categoryId;

    private String name;

    private String bizRegNo;

    private String contact;

    private String address;

    private String description;

    private Long reviewCount;

    private BigDecimal averageRating;

    private LocalDateTime createdAt;

}
