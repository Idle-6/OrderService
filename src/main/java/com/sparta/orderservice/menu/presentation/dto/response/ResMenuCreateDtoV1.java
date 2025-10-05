package com.sparta.orderservice.menu.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResMenuCreateDtoV1 {

    @NotNull
    private UUID id;

    private String name;

    private String description;

    private int price;

    @NotNull
    @JsonProperty("is_public")
    private boolean isPublic;
}
