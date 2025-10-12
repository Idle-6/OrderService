package com.sparta.orderservice.store.domain.repository;

import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CustomStoreRepository {

    Page<ResStoreDtoV1> getStorePage(SearchParam searchParam, Pageable pageable);

    Optional<ResStoreDetailDtoV1> getStoreById(UUID storeId);

    Optional<ResStoreDetailDtoV1> getStoreByUserId(Long userId);

}
