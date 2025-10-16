package com.sparta.orderservice.menu.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReqMenuUpdateDtoV1 {

    private String name;

    private String description;

    private int price;

    @NotNull
    @JsonProperty("is_public")
    private boolean isPublic;
}
