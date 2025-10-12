package com.sparta.orderservice.store.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreDtoV1;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreServiceV1 {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public ResStoreDetailDtoV1 createStore(ReqStoreDtoV1 request) {
        // todo: 회원 조회

        // todo: 회원이 가게를 가지고 있는 경우 예외처리


        Category category = categoryRepository.findById(request.getCategoryId())
                                                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

        // todo: 수정 필요
        Store store = Store.ofNewStore(request.getName(),
                request.getBizRegNo(),
                request.getContact(),
                request.getAddress(),
                request.getDescription(),
                request.isPublic(),
                category,
                null
        );

        return convertResStoreDetailDto(store);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public Page<ResStoreDtoV1> getStorePage(SearchParam search, Pageable pageable) {

        return storeRepository.getStorePage(search, pageable);
    }

    @Transactional(readOnly = true)
    public ResStoreDetailDtoV1 getStore(UUID storeId) {

        return storeRepository.getStoreById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));
    }

    public ResStoreDetailDtoV1 updateStore(UUID storeId, ReqStoreUpdateDtoV1 request) {

        Store store = storeRepository.findById(storeId)
                                        .orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));
        Category newCategory = null;
        if (request.getCategoryId() != null) {
            newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));
        }

        // todo: 수정필요
        store.update(request, newCategory, null);
        return convertResStoreDetailDto(store);
    }

    public void deleteStore(UUID storeId) {
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new IllegalArgumentException("해당 가게를 찾을 수 없습니다."));

        // todo: 수정필요
        store.delete(null);
    }

    private ResStoreDetailDtoV1 convertResStoreDetailDto(Store store) {
        return new ResStoreDetailDtoV1(store.getStoreId(), store.getCategory().getName(), store.getName(), store.getBizRegNo(), store.getContact(), store.getAddress(), store.getDescription(), store.isPublic(), store.getReviewCount(), store.getAverageRating(), store.getCreatedAt(), store.getUpdatedAt());
    }
}
