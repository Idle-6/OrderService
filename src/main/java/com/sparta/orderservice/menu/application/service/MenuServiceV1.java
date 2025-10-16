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
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceV1 {

    private final MenuRepository menuRepository;
    private final GeminiClient geminiClient;

    public ResMenuCreateDtoV1 createMenu(ReqMenuCreateDtoV1 requestDto) {

        MenuEntity menuEntity = MenuEntity.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .isPublic(requestDto.isPublic())
                .build();

        if(requestDto.isUseAi()) {
            ResGeminiDto resGeminiDto = geminiClient.callApi(requestDto.getPrompt());
            String description = resGeminiDto.getResultText();
            menuEntity.setDescription(description);
        } else {
            menuEntity.setDescription(requestDto.getDescription());
        }

        MenuEntity createdMenu = menuRepository.save(menuEntity);

        return ResMenuCreateDtoV1.builder()
                .id(createdMenu.getId())
                .name(createdMenu.getName())
                .description(createdMenu.getDescription())
                .price(createdMenu.getPrice())
                .isPublic(createdMenu.isPublic())
                .build();
    }

    public Page<ResMenuGetByStoreIdDtoV1> getMenuList(@AuthenticationPrincipal UserDetailsImpl userDetails, UUID storeId, String search, int page, int size, String sortBy, boolean isAsc) {

        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

//        logic : 레포지토리로 조회, 권한별 다른 로직
//            user : 해당 가게의 공개된 메뉴 리스트 조회
//            owner, admin : 해당 가게의 모든 메뉴 리스트 조회
        Page<MenuEntity> menuList = null;
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            String auth = authority.getAuthority();

            if (auth.equals(UserRoleEnum.ADMIN.getAuthority()) || auth.equals(UserRoleEnum.OWNER.getAuthority())) {
                menuList = menuRepository.findAllByStoreIdAndNameLike(storeId, search, pageable);
                break;
            } else if (auth.equals(UserRoleEnum.USER.getAuthority())) {
                menuList = menuRepository.findAllByStoreIdAndIsPublicTrueAndNameLike(storeId, search, pageable);
                break;
            }
        }

        if(menuList == null){
            //TODO 메뉴 리스트 조회 - 권한 없음 예외처리
        }

//        convert : Page<entity> -> Page<dto>
        return menuList.map(menuEntity ->
                ResMenuGetByStoreIdDtoV1.builder()
                        .id(menuEntity.getId())
                        .name(menuEntity.getName())
                        .description(menuEntity.getDescription())
                        .price(menuEntity.getPrice())
                        .isPublic(menuEntity.isPublic())
                        .build()
        );
    }

    public ResMenuGetDtoV1 getMenu(UUID menuId) {
//        logic : 레포지토리로 조회
        MenuEntity menuEntity = menuRepository.findById(menuId).orElseThrow(() -> new NullPointerException());

//        convert : entity -> dto
        return ResMenuGetDtoV1.builder()
                .id(menuEntity.getId())
                .name(menuEntity.getName())
                .description(menuEntity.getDescription())
                .price(menuEntity.getPrice())
                .isPublic(menuEntity.isPublic())
                .build();
    }

    public void updateMenu(UUID menuId, ReqMenuUpdateDtoV1 requestDto) {

//        logic : 레포지토리로 수정
        MenuEntity menuEntity = menuRepository.findById(menuId).orElseThrow(() -> new NullPointerException());
        menuEntity.setName(requestDto.getName());
        menuEntity.setDescription(requestDto.getDescription());
        menuEntity.setPrice(requestDto.getPrice());
        menuEntity.setPublic(requestDto.isPublic());
        menuRepository.save(menuEntity);
    }

    public void deleteMenu(UUID menuId) {
//        logic : 레포지토리로 삭제
        MenuEntity menuEntity = menuRepository.findById(menuId).orElseThrow(() -> new NullPointerException());
        menuRepository.delete(menuEntity);
    }
}
