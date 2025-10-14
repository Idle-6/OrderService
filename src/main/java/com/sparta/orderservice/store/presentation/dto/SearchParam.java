package com.sparta.orderservice.store.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchParam {

    private String term;

    private UUID categoryId;

}
