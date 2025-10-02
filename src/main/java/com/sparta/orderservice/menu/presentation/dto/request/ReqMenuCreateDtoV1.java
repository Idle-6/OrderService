package com.sparta.orderservice.menu.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReqMenuCreateDtoV1 {

    private String name;

    private String description;

    private int price;

    @NotNull
    private boolean isPublic;

    @NotNull
    private boolean isUseAi;

    private String prompt;

    @AssertTrue(message = "ai를 사용하려면 프롬프트가 있어야 합니다.")
    public boolean isValidAi() {
        if(isUseAi)
            if(prompt == null || prompt.isBlank()){
                return false;
            }
        return true;
    }
}
