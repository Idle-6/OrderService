package com.sparta.orderservice.menu.application.service;

import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.menu.domain.repository.MenuRepository;
import com.sparta.orderservice.menu.infrastructure.api.gemini.client.GeminiClient;
import com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response.ResGeminiDto;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuUpdateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuGetByStoreIdDtoV1;
import com.sparta.orderservice.menu.presentation.dto.response.ResMenuGetDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("메뉴 서비스")
class MenuServiceV1Test {

    @Mock
    GeminiClient geminiClient;

    @Mock
    MenuRepository menuRepository;

    @InjectMocks
    MenuServiceV1 menuServiceV1;

    @Test
    @DisplayName("메뉴 생성")
    void testCreateMenu() {
        //before
        ReqMenuCreateDtoV1 requestDto = ReqMenuCreateDtoV1.builder()
                .name("짬뽕")
                .description("매움")
                .price(10000)
                .isPublic(true)
                .isUseAi(false)
                .prompt("")
                .build();

        when(menuRepository.save(any(MenuEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0);
                });

        //when
        ResMenuCreateDtoV1 res = menuServiceV1.createMenu(requestDto);

        //then
        verify(geminiClient, times(0)).callApi(anyString());
        verify(menuRepository, times(1)).save(any(MenuEntity.class));
        assertEquals("매움", res.getDescription());
    }

    @Test
    @DisplayName("메뉴 생성 - AI 사용")
    void testCreateMenuWithAi() {
        //before
        ReqMenuCreateDtoV1 requestDto = ReqMenuCreateDtoV1.builder()
                .name("짬뽕")
                .description("매움")
                .price(10000)
                .isPublic(true)
                .isUseAi(true)
                .prompt("짬뽕 상품의 설명을 50자 이내로 추천해줘.")
                .build();

        when(geminiClient.callApi(any(String.class)))
                .thenReturn(new ResGeminiDto("얼큰하고 시원한 국물의 정통 짬뽕"));

        when(menuRepository.save(any(MenuEntity.class)))
                .thenAnswer(invocationOnMock -> {
                    return invocationOnMock.getArgument(0);
                });

        //when
        ResMenuCreateDtoV1 res = menuServiceV1.createMenu(requestDto);

        //then
        verify(geminiClient, times(1)).callApi(anyString());
        verify(menuRepository, times(1)).save(any(MenuEntity.class));
        assertEquals("얼큰하고 시원한 국물의 정통 짬뽕", res.getDescription());
    }

    @Test
    @DisplayName("메뉴 리스트 조회 - USER")
    void getMenuListUser() {
        //before
        User user = User.builder().role(UserRoleEnum.USER).build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UUID storeId = UUID.randomUUID();
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = false;

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<MenuEntity> menuList = Arrays.asList(
                new MenuEntity(UUID.randomUUID(), "자장면", "", 9000, true, storeId),
                new MenuEntity(UUID.randomUUID(), "짬뽕", "신라면 정도 매움", 10000, true, storeId),
                new MenuEntity(UUID.randomUUID(), "탕수육", "쫄깃 바삭한 튀김", 15000, true, storeId),
                new MenuEntity(UUID.randomUUID(), "우육면", "", 9000, false, storeId),
                new MenuEntity(UUID.randomUUID(), "팔보채", "", 15000, true, storeId)
        );

        when(menuRepository.findAllByStoreIdAndIsPublicTrue(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(menuList, pageable, menuList.size()));

        //when
        Page<ResMenuGetByStoreIdDtoV1> res = menuServiceV1.getMenuList(userDetails, storeId, page, size, sortBy, isAsc);

        //then
        verify(menuRepository, times(1)).findAllByStoreIdAndIsPublicTrue(any(UUID.class), any(Pageable.class));
        verify(menuRepository, times(0)).findAllByStoreId(any(UUID.class), any(Pageable.class));
        assertNotNull(res);
        //TODO 정렬 확인
    }

    @Test
    @DisplayName("메뉴 리스트 조회 - ADMIN")
    void getMenuListAdmin() {
        //before
        User user = User.builder().role(UserRoleEnum.ADMIN).build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UUID storeId = UUID.randomUUID();
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = false;

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        List<MenuEntity> menuList = Arrays.asList(
                new MenuEntity(UUID.randomUUID(), "자장면", "", 9000, true, storeId),
                new MenuEntity(UUID.randomUUID(), "짬뽕", "신라면 정도 매움", 10000, true, storeId),
                new MenuEntity(UUID.randomUUID(), "탕수육", "쫄깃 바삭한 튀김", 15000, true, storeId),
                new MenuEntity(UUID.randomUUID(), "우육면", "", 9000, false, storeId),
                new MenuEntity(UUID.randomUUID(), "팔보채", "", 15000, true, storeId)
        );

        when(menuRepository.findAllByStoreId(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(menuList, pageable, menuList.size()));

        //when
        Page<ResMenuGetByStoreIdDtoV1> res = menuServiceV1.getMenuList(userDetails, storeId, page, size, sortBy, isAsc);

        //then
        verify(menuRepository, times(0)).findAllByStoreIdAndIsPublicTrue(any(UUID.class), any(Pageable.class));
        verify(menuRepository, times(1)).findAllByStoreId(any(UUID.class), any(Pageable.class));
        assertNotNull(res);
        //TODO 정렬 확인
    }

    @Test
    void getMenu() {
        //before
        UUID menuId = UUID.randomUUID();
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuId);

        when(menuRepository.findById(any(UUID.class)))
                .thenReturn(Optional.of(menuEntity));

        //when
        ResMenuGetDtoV1 res = menuServiceV1.getMenu(menuId);

        //then
        assertNotNull(res);
        assertEquals(menuId, res.getId());
    }

    @Test
    void updateMenu() {
        //before
        UUID menuId = UUID.randomUUID();
        ReqMenuUpdateDtoV1 requestDto = ReqMenuUpdateDtoV1.builder()
                .name("해물짬뽕")
                .description("해물 듬뿍")
                .price(12000)
                .isPublic(true)
                .build();

        when(menuRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(new MenuEntity()));

        when(menuRepository.save(any(MenuEntity.class)))
        .thenReturn(new MenuEntity());

        //when
        menuServiceV1.updateMenu(menuId, requestDto);

        //then
        verify(menuRepository, times(1)).findById(any(UUID.class));
        verify(menuRepository, times(1)).save(any(MenuEntity.class));
    }

    @Test
    void deleteMenu() {
        //before
        UUID menuId = UUID.randomUUID();

        when(menuRepository.findById(any(UUID.class)))
        .thenReturn(Optional.of(new MenuEntity()));

        //when
        menuServiceV1.deleteMenu(menuId);

        //then
        verify(menuRepository, times(1)).findById(any(UUID.class));
        verify(menuRepository, times(1)).delete(any(MenuEntity.class));
    }
}