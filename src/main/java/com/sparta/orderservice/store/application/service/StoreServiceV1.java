package com.sparta.orderservice.store.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.category.presentation.advice.CategoryErrorCode;
import com.sparta.orderservice.category.presentation.advice.CategoryException;
import com.sparta.orderservice.category.presentation.advice.CategoryExceptionLogUtils;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.store.presentation.advice.StoreErrorCode;
import com.sparta.orderservice.store.presentation.advice.StoreException;
import com.sparta.orderservice.store.presentation.advice.StoreExceptionLogUtils;
import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreDtoV1;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreServiceV1 {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

    public ResStoreDetailDtoV1 createStore(ReqStoreDtoV1 request, User user) {
        Long userId = user.getUserId();
        if(storeRepository.existsStoreByUserId(userId)) {
            throw new StoreException(
                    StoreErrorCode.STORE_ALREADY_OWNED,
                    StoreExceptionLogUtils.getAlreadyOwnedMessage(userId)
            );
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(
                        CategoryErrorCode.CATEGORY_NOT_FOUND,
                        CategoryExceptionLogUtils.getNotFoundMessage(request.getCategoryId(), userId)
                ));

        Store store = Store.ofNewStore(request.getName(),
                request.getBizRegNo(),
                request.getContact(),
                request.getAddress(),
                request.getDescription(),
                request.isPublic(),
                category,
                user
        );

        storeRepository.save(store);

        return convertResStoreDetailDto(store);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public Page<ResStoreDtoV1> getStorePage(SearchParam search, Pageable pageable) {

        return storeRepository.findStorePage(search, pageable, false);
    }

    @Transactional(readOnly = true)
    public ResStoreDetailDtoV1 getStore(UUID storeId) {

        return storeRepository.findStoreDetailById(storeId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(storeId)
                ));
    }

    @Transactional(readOnly = true)
    public ResStoreDetailDtoV1 getStoreForOwner(Long userId) {

        return storeRepository.findStoreDetailByUserId(userId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNoOwnedStoreErrorMessage(userId)
                ));
    }

    public ResStoreDetailDtoV1 updateStore(UUID storeId, ReqStoreUpdateDtoV1 request, User user) {
        Long userId = user.getUserId();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(storeId, userId)
                ));

        if (!hasPermission(user, store)) {
            throw new StoreException(
                    StoreErrorCode.STORE_FORBIDDEN,
                    StoreExceptionLogUtils.getUpdateForbiddenMessage(storeId, userId)
            );
        }

        Category newCategory = null;
        if (request.getCategoryId() != null) {
            newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryException(
                            CategoryErrorCode.CATEGORY_NOT_FOUND,
                            CategoryExceptionLogUtils.getNotFoundMessage(request.getCategoryId(), userId)
                    ));
        }

        store.update(request, newCategory, userId);
        return convertResStoreDetailDto(store);
    }

    public void deleteStore(UUID storeId, User user) {
        Long userId = user.getUserId();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(storeId, userId)
                ));
        if (!hasPermission(user, store)) {
            throw new StoreException(
                    StoreErrorCode.STORE_FORBIDDEN,
                    StoreExceptionLogUtils.getDeleteForbiddenMessage(storeId, userId)
            );
        }

        store.delete(userId);
    }

    private ResStoreDetailDtoV1 convertResStoreDetailDto(Store store) {
        return new ResStoreDetailDtoV1(
                store.getStoreId(),
                store.getCategory().getName(),
                store.getName(),
                store.getBizRegNo(),
                store.getContact(),
                store.getAddress(),
                store.getDescription(),
                store.isPublic(),
                store.getReviewCount(),
                store.getAverageRating(),
                store.getCreatedAt(),
                store.getUpdatedAt()
        );
    }

    private boolean hasPermission(User user, Store store) {
        boolean isAdmin = Objects.equals(user.getRole(), UserRoleEnum.ADMIN);
        boolean isOwner = Objects.equals(store.getCreatedBy().getUserId(), user.getUserId());

        return isAdmin || isOwner;
    }
}