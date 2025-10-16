package com.sparta.orderservice.manage.application.service;

import com.sparta.orderservice.manage.domain.repository.ManageStoreRepository;
import com.sparta.orderservice.manage.domain.repository.ManageUserRepository;
import com.sparta.orderservice.manage.presentation.dto.response.*;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManageServiceV1 {

    private final ManageUserRepository userRepository;
    private final ManageStoreRepository storeRepository;
//    private final ManageOrderRepository orderRepository;


    private Pageable toPageable(int page, int pageSize, String sortBy, boolean isAsc) {
        int pageIndex = Math.max(page - 1, 0);
        int size = Math.min(Math.max(pageSize, 1), 100);

        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return PageRequest.of(pageIndex, size, sort);
    }

    /* 회원 조회 */
    public List<ResUserDtoV1> getUserList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        Pageable pageable = toPageable(page, pageSize, sortBy, isAsc);

        Page<User> result = StringUtils.hasText(search)
                ? userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable)
                : userRepository.findAll(pageable);

        return result.map(this::toResUserDto).getContent();
    }

    private ResUserDtoV1 toResUserDto(User user) {
        return new ResUserDtoV1(
                String.valueOf(user.getUserId()),
                user.getName(),
                user.getEmail(),
                user.getAddress(),
                user.isActive() ? "활성화" : "비활성화"
        );
    }

    /* 회원 상세 조회 */
    public ResUserDetailDtoV1 getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. userId=" + userId));

        // ============================
        // 주문 리스트는 추가 예정 !!!!!!!!!
        List<orderDtoV1> orderList = new ArrayList<>();
        // =============================

        return ResUserDetailDtoV1.builder()
                .userId(String.valueOf(user.getUserId()))
                .name(user.getName())
                .email(user.getEmail())
                .address(user.getAddress())
                .status(user.isActive() ? "활성화" : "비활성화")
                .orderMenuList(orderList)
                .createdAt(user.getCreatedAt())
                .updateAt(user.getUpdatedAt())
                .createBy(String.valueOf(user.getCreatedBy()))
                .updateBy(user.getUpdatedBy() != null ? String.valueOf(user.getUpdatedBy()) : null)
                .build();
    }

    /* 회원 비활성화 */
    @Transactional
    public void userDeactive(Long userId) {
        int updated = userRepository.deactivate(
                userId,
                LocalDateTime.now(),
                userId // 관리자 ID로 수정 !!!!!!!
                //=============================================================
        );
        if (updated == 0) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다. userId=" + userId);
        }
    }

    /* 회원 활성화 */
    @Transactional
    public void userActive(Long userId) {
        int updated = userRepository.activate(
                userId,
                LocalDateTime.now(),
                userId // 관리자 ID로 수정 !!!!!!!
                //=============================================================
        );
        if (updated == 0) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다. userId=" + userId);
        }
    }

    /* 가게 조회 */
    public List<ResStoreDtoV1> getStoreList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        Pageable pageable = toPageable(page, pageSize, sortBy, isAsc);

        Page<Store> result = StringUtils.hasText(search)
                ? storeRepository.findByNameContainingIgnoreCase(search, pageable)
                : storeRepository.findAll(pageable);

        return result.stream()
                .map(this::toResStoreDto)
                .collect(Collectors.toList());
    }

    private ResStoreDtoV1 toResStoreDto(Store store) {
        return new ResStoreDtoV1(
                store.getStoreId().toString(),
                store.getName(),
                store.getCategory() != null ? store.getCategory().getName() : null,
                store.getAddress(),
                store.isPublic() ? "활성화" : "비활성화"
        );
    }

    /* 가게 상세 조회 */
    public ResStoreDetailDtoV1 getStore(UUID storeId) {
        Store store = storeRepository.findByStoreId(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다. storeId=" + storeId));

        return toResStoreDetailDto(store);
    }

    private ResStoreDetailDtoV1 toResStoreDetailDto(Store store) {
        return ResStoreDetailDtoV1.builder()
                .storeId(store.getStoreId().toString())
                .storeName(store.getName())
                .category(store.getCategory() != null ? store.getCategory().getName() : null)
                .contact(store.getContact())
                .address(store.getAddress())
                .description(store.getDescription())
                .status(store.isPublic() ? "활성화" : "비활성화")
                .createdAt(store.getCreatedAt())
                .updateAt(store.getUpdatedAt())
                .createBy(store.getCreatedBy() != null ?
                        store.getCreatedBy().getUserId().toString() : null)
                .updateBy(store.getUpdatedBy() != null ?
                        store.getUpdatedBy().toString() : null)
                .build();
    }

    /* 가게 비활성화 */
    @Transactional
    public void storeDeactive(UUID storeId) {
        int updated = storeRepository.deactivateStore(
                storeId,
                LocalDateTime.now(),
                1L // 관리자 ID로 수정 !!!!!!!
                //=============================================================
        );

        if (updated == 0) {
            throw new IllegalArgumentException("가게를 찾을 수 없습니다. storeId=" + storeId);
        }
    }

    /* 가게 활성화 */
    @Transactional
    public void storeActive(UUID storeId) {
        int updated = storeRepository.activateStore(
                storeId,
                LocalDateTime.now(),
                1L // 관리자 ID로 수정 !!!!!!!
                //=============================================================
        );

        if (updated == 0) {
            throw new IllegalArgumentException("가게를 찾을 수 없습니다. storeId=" + storeId);
        }
    }

    /* 주문 리스트 조회 */
    public List<ResOrderDtoV1> getOrderList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        List<ResOrderDtoV1> orderDtoList = new ArrayList<>();

        orderDtoList.add(ResOrderDtoV1.builder()
                .orderId("O1001")
                .customerId("123")
                .storeId("S001")
                .totalPrice(25000)
                .orderStatus("주문완료")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build()
        );

        orderDtoList.add(ResOrderDtoV1.builder()
                .orderId("O1002")
                .customerId("456")
                .storeId("S002")
                .totalPrice(18000)
                .orderStatus("배송중")
                .createdAt(LocalDateTime.now().minusHours(10))
                .build()
        );

        return orderDtoList;
    }

    /* 주문 상세 조회 */
    public ResOrderDetailDtoV1 getOrder(Integer orderId) {
        ResOrderDetailDtoV1 orderDetail = ResOrderDetailDtoV1.builder()
                .orderId(orderId.toString())
                .customerId("123")
                .storeId("S001")
                .oderMessage("문 앞에 두고 가주세요")
                .orderMenuList(List.of(
                        new orderMenuDtoV1("1001", "김치찌개", 2, 18000),
                        new orderMenuDtoV1("1002", "된장찌개", 1, 8000)
                ))
                .totalPrice(26000)
                .orderStatus("주문완료")
                .createdAt(LocalDateTime.now().minusDays(2))
                .deleteAt(null)
                .createBy("user1")
                .deleteBy(null)
                .build();
        return orderDetail;
    }
}
