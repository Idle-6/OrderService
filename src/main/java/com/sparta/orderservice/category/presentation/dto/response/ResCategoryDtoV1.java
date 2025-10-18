package com.sparta.orderservice.category.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResCategoryDtoV1 {

    private UUID id;

    private String name;
}
