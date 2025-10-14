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
import com.sparta.orderservice.user.domain.repository.UserRepository;
import com.sparta.orderservice.user.infrastructure.UserThreadLocal;
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
    private final UserRepository userRepository;
    private final Long USER_ID = UserThreadLocal.getUserId();

    public ResStoreDetailDtoV1 createStore(ReqStoreDtoV1 request) {
        if(storeRepository.existsStoreByUserId(USER_ID)) {
            throw new StoreException(
                    StoreErrorCode.STORE_ALREADY_OWNED,
                    StoreExceptionLogUtils.getAlreadyOwnedMessage(USER_ID)
            );
        }

        User user = userRepository.findById(USER_ID).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryException(
                        CategoryErrorCode.CATEGORY_NOT_FOUND,
                        CategoryExceptionLogUtils.getNotFoundMessage(request.getCategoryId(), null)
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

        return storeRepository.findStorePage(search, pageable);
    }

    @Transactional(readOnly = true)
    public ResStoreDetailDtoV1 getStore(UUID storeId) {

        return storeRepository.findStoreDetailById(storeId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(storeId, null)
                ));
    }

    public ResStoreDetailDtoV1 updateStore(UUID storeId, ReqStoreUpdateDtoV1 request) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(storeId, null)
                ));
        Category newCategory = null;
        if (request.getCategoryId() != null) {
            newCategory = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryException(
                            CategoryErrorCode.CATEGORY_NOT_FOUND,
                            CategoryExceptionLogUtils.getNotFoundMessage(request.getCategoryId(), null)
                    ));
        }

        store.update(request, newCategory, USER_ID);
        return convertResStoreDetailDto(store);
    }

    public void deleteStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(
                        StoreErrorCode.STORE_NOT_FOUND,
                        StoreExceptionLogUtils.getNotFoundMessage(storeId, null)
                ));
        User user = userRepository.findById(UserThreadLocal.getUserId()).orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (!hasDeletePermission(user, store, USER_ID)) {
            throw new StoreException(
                    StoreErrorCode.STORE_DELETE_FORBIDDEN,
                    StoreExceptionLogUtils.getDeleteForbiddenMessage(USER_ID)
            );
        }

        store.delete(USER_ID);
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

    private boolean hasDeletePermission(User user, Store store, Long userId) {
        boolean isAdmin = Objects.equals(user.getRole().getAuthority(), UserRoleEnum.ADMIN.getAuthority());
        boolean isOwner = Objects.equals(store.getCreatedBy().getUserId(), userId);
        return isAdmin || isOwner;
    }
}