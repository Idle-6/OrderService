package com.sparta.orderservice.menu.presentation.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @JsonProperty("is_public")
    private boolean isPublic;

    @NotNull
    @JsonProperty("is_use_ai")
    private boolean isUseAi;

    @Size(max = 50, message = "프롬프트는 50자 이내로 적어주세요")
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
