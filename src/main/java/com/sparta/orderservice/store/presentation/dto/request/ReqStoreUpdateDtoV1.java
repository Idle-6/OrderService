package com.sparta.orderservice.store.presentation.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReqStoreUpdateDtoV1 {

    private String name;

    @Pattern(regexp = "\\d{3}-\\d{2}-\\d{5}", message = "사업자 번호는 XXX-XX-XXXXX 형식이어야 합니다.")
    private String bizRegNo;

    @Pattern(
            regexp = "^(01[016789]-\\d{3,4}-\\d{4}|02-\\d{3,4}-\\d{4}|0\\d{2}-\\d{3,4}-\\d{4})$",
            message = "유효한 전화번호(휴대폰 또는 가게 전화번호)를 하이픈 포함 형식으로 입력하세요."
    )
    private String contact;

    private String address;

    private String description;

    private boolean isPublic;

}
