package com.sparta.orderservice.manage.presentation.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class orderMenuDtoV1 {
    private String menuId;
    private String menu;
    private Integer amount;
    private Integer price;
}
