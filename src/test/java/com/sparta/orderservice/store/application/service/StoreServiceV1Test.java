package com.sparta.orderservice.store.application.service;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.category.domain.repository.CategoryRepository;
import com.sparta.orderservice.category.presentation.advice.CategoryException;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.domain.repository.StoreRepository;
import com.sparta.orderservice.store.presentation.advice.StoreException;
import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreDtoV1;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceV1Test {

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    StoreRepository storeRepository;

    @InjectMocks
    StoreServiceV1 storeServiceV1;

    Category category;
    Store store;
    UUID storeId, categoryId;

    @BeforeEach
    void setUp() {
        User admin = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        ReflectionTestUtils.setField(admin, "userId", 1L);

        category = Category.ofNewCategory("한식", admin.getUserId());
        categoryId = UUID.randomUUID();
        ReflectionTestUtils.setField(category, "categoryId", categoryId);

        store = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category, admin);
        storeId = UUID.randomUUID();
        ReflectionTestUtils.setField(store, "storeId", storeId);
    }

    @Test
    @DisplayName("가게 생성")
    void createStore() {
        // todo: 회원 조회

        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));

        ReqStoreDtoV1 request = new ReqStoreDtoV1(categoryId, "가게이름2", "223-45-67890", "010-2222-2222", "서울 마포구 연남동", "시원한 한식", false);
        storeServiceV1.createStore(request);

        verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
        verify(storeRepository, Mockito.times(1)).save(Mockito.any(Store.class));
    }

    @Test
    @DisplayName("가게 조회 - 페이징")
    void getStorePage() {
        SearchParam searchParam = new SearchParam();
        ResStoreDtoV1 response = new ResStoreDtoV1(UUID.randomUUID(), "한식", "가게이름1", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", 999L, BigDecimal.valueOf(4.5));
        when(storeRepository.findStorePage(Mockito.any(SearchParam.class), Mockito.any())).thenReturn(new PageImpl<>(List.of(response)));

        storeServiceV1.getStorePage(searchParam, Pageable.ofSize(5));

        verify(storeRepository, Mockito.times(1)).findStorePage(Mockito.any(SearchParam.class), Mockito.any());

    }

    @Test
    @DisplayName("가게 조회")
    void getStore() {
        UUID storeId = UUID.randomUUID();
        ResStoreDetailDtoV1 response = new ResStoreDetailDtoV1(storeId, "한식", "가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, 10L, BigDecimal.valueOf(4.8), LocalDateTime.now(), LocalDateTime.now());
        when(storeRepository.findStoreDetailById(Mockito.any())).thenReturn(Optional.of(response));

        storeServiceV1.getStore(storeId);

        verify(storeRepository, Mockito.times(1)).findStoreDetailById(Mockito.any());
    }

    @Test
    @DisplayName("가게 조회 - 존재하지 않음")
    void getStore_not_found() {
        when(storeRepository.findStoreDetailById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(StoreException.class, () -> storeServiceV1.getStore(storeId));

        verify(storeRepository, Mockito.times(1)).findStoreDetailById(Mockito.any());
    }

    @Test
    @DisplayName("가게 수정 - 이름")
    void updateStore() {
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));

        ReqStoreUpdateDtoV1 request = new ReqStoreUpdateDtoV1(null, "가게2", null, null, null, null, true);
        storeServiceV1.updateStore(storeId, request);

        verify(storeRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("가게 수정 - 존재하지 않음")
    void updateStore_not_found() {
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(StoreException.class, () -> {
            ReqStoreUpdateDtoV1 request = new ReqStoreUpdateDtoV1(null, "가게2", null, null, null, null, true);
            storeServiceV1.updateStore(storeId, request);
        });

        verify(storeRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("가게 수정 - 카테고리")
    void updateStore_category() {
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));
        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.of(category));

        ReqStoreUpdateDtoV1 request = new ReqStoreUpdateDtoV1(categoryId, "가게2", null, null, null, null,  true);
        storeServiceV1.updateStore(storeId, request);

        verify(storeRepository, Mockito.times(1)).findById(Mockito.any());
        verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("가게 수정 - 존재하지 않는 카테고리")
    void updateStore_category_not_found() {
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));
        when(categoryRepository.findById(Mockito.any())).thenReturn(Optional.empty());

        assertThrows(CategoryException.class, () -> {
            ReqStoreUpdateDtoV1 request = new ReqStoreUpdateDtoV1(UUID.randomUUID(), "가게2", null, null, null, null, true);
            storeServiceV1.updateStore(storeId, request);
        });

        verify(storeRepository, Mockito.times(1)).findById(Mockito.any());
        verify(categoryRepository, Mockito.times(1)).findById(Mockito.any());
    }

    @Test
    @DisplayName("가게 삭제")
    void deleteStore() {
        when(storeRepository.findById(Mockito.any())).thenReturn(Optional.of(store));

        storeServiceV1.deleteStore(storeId);

        verify(storeRepository, Mockito.times(1)).findById(Mockito.any());
    }
}