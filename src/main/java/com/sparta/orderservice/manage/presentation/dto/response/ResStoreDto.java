package com.sparta.orderservice.manage.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResStoreDto {
    private String storeId;
    private String storeName;
    private String category;
    private String address;
    private String status;

}
