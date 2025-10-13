package com.sparta.orderservice.menu.presentation.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResMenuGetByStoreIdDtoV1 {

    @NotNull
    private UUID id;

    private String name;

    private String description;

    private int price;

    @NotNull
    private boolean isPublic;
}
