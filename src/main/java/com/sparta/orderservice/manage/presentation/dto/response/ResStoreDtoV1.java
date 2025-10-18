package com.sparta.orderservice.manage.presentation.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResStoreDtoV1 {
    private String storeId;
    private String storeName;
    private String category;
    private String address;
    private String status;

}
