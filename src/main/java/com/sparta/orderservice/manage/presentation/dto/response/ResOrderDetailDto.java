package com.sparta.orderservice.manage.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResOrderDetailDto {
    private String orderId;
    private String customerId;
    private String storeId;
    private String oderMessage;
    private List<orderMenuDto> orderMenuList;
    private Integer totalPrice;
    private String orderStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deleteAt;
    private String createBy;
    private String deleteBy;
}
