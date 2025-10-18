package com.sparta.orderservice.category.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqCategoryDtoV1 {

    @NotBlank(message = "카테고리명은 필수 입력 항목입니다.")
    private  String name;
}
