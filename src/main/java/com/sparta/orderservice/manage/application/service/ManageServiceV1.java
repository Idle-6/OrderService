package com.sparta.orderservice.manage.application.service;

import com.sparta.orderservice.manage.domain.repository.ManageOrderMenuRepository;
import com.sparta.orderservice.manage.domain.repository.ManageOrderRepository;
import com.sparta.orderservice.manage.domain.repository.ManageStoreRepository;
import com.sparta.orderservice.manage.domain.repository.ManageUserRepository;
import com.sparta.orderservice.manage.presentation.advice.exception.ManageException;
import com.sparta.orderservice.manage.presentation.dto.response.*;
import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.order.domain.entity.OrderMenu;
import com.sparta.orderservice.order.domain.entity.OrderStatus;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManageServiceV1 {

    private final ManageUserRepository userRepository;
    private final ManageStoreRepository storeRepository;
    private final ManageOrderRepository orderRepository;
    private final ManageOrderMenuRepository orderMenuRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS =
            Set.of("orderId", "createdAt", "updatedAt", "totalPrice", "orderStatus", "id", "name", "email");

    private Pageable toPageable(int page, int pageSize, String sortBy, boolean isAsc) {
        if (page < 1 || pageSize < 1) {
            throw ManageException.invalidInput("page와 pageSize는 1 이상이어야 합니다.");
        }
        // 정렬 필드 검증(옵션)
        if (StringUtils.hasText(sortBy) && !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw ManageException.invalidSortField(sortBy);
        }

        int pageIndex = page - 1;
        int size = Math.min(pageSize, 100);
        Sort sort = Sort.by(isAsc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        return PageRequest.of(pageIndex, size, sort);
    }

    /* 회원 조회 */
    @Transactional(readOnly = true)
    public List<ResUserDtoV1> getUserList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        Pageable pageable = toPageable(page, pageSize, sortBy, isAsc);

        try {
            Page<User> result = StringUtils.hasText(search)
                    ? userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable)
                    : userRepository.findAll(pageable);

            if (result.isEmpty()) throw ManageException.notFound("회원 목록");

            return result.map(this::toResUserDto).getContent();

        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
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
    @Transactional(readOnly = true)
    public ResUserDetailDtoV1 getUser(Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> ManageException.notFound("사용자 userId=" + userId));

            List<Order> orders = orderRepository.findByUser_UserId(userId);
            List<orderDtoV1> orderList = orders.stream()
                    .map(order -> new orderDtoV1(order.getOrderId().toString()))
                    .toList();

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
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    /* 회원 비활성화 */
    @Transactional
    public void userDeactive(Long adminId, Long userId) {
        if (adminId == null) throw ManageException.unauthorized("관리자 인증 정보가 없습니다.");
        try {
            int updated = userRepository.deactivate(userId, LocalDateTime.now(), adminId);
            if (updated == 0) throw ManageException.notFound("사용자 userId=" + userId);
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    /* 회원 활성화 */
    @Transactional
    public void userActive(Long adminId, Long userId) {
        if (adminId == null) throw ManageException.unauthorized("관리자 인증 정보가 없습니다.");
        try {
            int updated = userRepository.activate(userId, LocalDateTime.now(), adminId);
            if (updated == 0) throw ManageException.notFound("사용자 userId=" + userId);
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    /* 가게 조회 */
    @Transactional(readOnly = true)
    public List<ResStoreDtoV1> getStoreList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        Pageable pageable = toPageable(page, pageSize, sortBy, isAsc);
        try {
            Page<Store> result = StringUtils.hasText(search)
                    ? storeRepository.findByNameContainingIgnoreCase(search, pageable)
                    : storeRepository.findAll(pageable);

            if (result.isEmpty()) {
                throw ManageException.notFound("가게 목록");
            }

            return result.stream().map(this::toResStoreDto).toList();
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
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
    @Transactional(readOnly = true)
    public ResStoreDetailDtoV1 getStore(UUID storeId) {
        try {
            Store store = storeRepository.findByStoreId(storeId)
                    .orElseThrow(() -> ManageException.notFound("가게 storeId=" + storeId));

            return toResStoreDetailDto(store);
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
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
    public void storeDeactive(Long adminId, UUID storeId) {
        if (adminId == null) throw ManageException.unauthorized("관리자 인증 정보가 없습니다.");
        try {
            int updated = storeRepository.deactivateStore(storeId, LocalDateTime.now(), adminId);
            if (updated == 0) throw ManageException.notFound("가게 storeId=" + storeId);
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    /* 가게 활성화 */
    @Transactional
    public void storeActive(Long adminId, UUID storeId) {
        if (adminId == null) throw ManageException.unauthorized("관리자 인증 정보가 없습니다.");
        try {
            int updated = storeRepository.activateStore(storeId, LocalDateTime.now(), adminId);
            if (updated == 0) throw ManageException.notFound("가게 storeId=" + storeId);
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    /* 주문 리스트 조회 */
    @Transactional(readOnly = true)
    public List<ResOrderDtoV1> getOrderList(String search, int page, int pageSize, String sortBy, boolean isAsc) {
        Pageable pageable = toPageable(page, pageSize, sortBy, isAsc);
        try {
            Specification<Order> spec = buildOrderSearchSpec(search);
            Page<Order> result = orderRepository.findAll(spec, pageable);
            if (result.isEmpty()) {
                throw ManageException.notFound("주문 목록");
            }
            return result.stream().map(this::toResOrderDto).toList();
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    private Specification<Order> buildOrderSearchSpec(String search) {
        if (!StringUtils.hasText(search)) return (root, query, cb) -> cb.conjunction();

        return (root, query, cb) -> {
            List<Predicate> orList = new ArrayList<>();

            try { // orderId(UUID) 정확일치 시도
                UUID oid = UUID.fromString(search.trim());
                orList.add(cb.equal(root.get("orderId"), oid));
            } catch (IllegalArgumentException ignore) {}

            // 이름, 가게, 메세지로 검색
            orList.add(cb.like(cb.lower(root.get("user").get("name")), "%" + search.toLowerCase() + "%"));
            orList.add(cb.like(cb.lower(root.get("store").get("name")), "%" + search.toLowerCase() + "%"));
            orList.add(cb.like(cb.lower(root.get("orderMessage")), "%" + search.toLowerCase() + "%"));

            return cb.or(orList.toArray(new Predicate[0]));
        };
    }

    private ResOrderDtoV1 toResOrderDto(Order order) {
        return ResOrderDtoV1.builder()
                .orderId(order.getOrderId().toString())
                .customerId(order.getUser() != null ? String.valueOf(order.getUser().getUserId()) : null)
                .storeId(order.getStore() != null ? order.getStore().getStoreId().toString() : null)
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus().toString())
                .createdAt(order.getCreatedAt())
                .build();
    }

    /* 주문 상세 조회 */
    @Transactional(readOnly = true)
    public ResOrderDetailDtoV1 getOrder(UUID orderId) {
        try {
            Order order = orderRepository.findByOrderId(orderId)
                    .orElseThrow(() -> ManageException.notFound("주문 orderId=" + orderId));

            // 주의: orderMenus LAZY라면 레포에 @EntityGraph로 미리 패치하는 것을 권장합니다.
            List<orderMenuDtoV1> items = order.getOrderMenus().stream()
                    .map(this::toOrderMenuDto)
                    .toList();

            return ResOrderDetailDtoV1.builder()
                    .orderId(order.getOrderId().toString())
                    .customerId(order.getUser() != null ? String.valueOf(order.getUser().getUserId()) : null)
                    .storeId(order.getStore() != null ? order.getStore().getStoreId().toString() : null)
                    .oderMessage(order.getOrderMessage())
                    .orderMenuList(items)
                    .totalPrice(order.getTotalPrice()) // 주문 엔티티에 저장된 금액 사용
                    .orderStatus(order.getOrderStatus().name())
                    .createdAt(order.getCreatedAt())
                    .deleteAt(order.getDeletedAt())
                    .createBy(order.getCreatedBy() != null ? String.valueOf(order.getCreatedBy().getUserId()) : null)
                    .deleteBy(order.getDeletedBy() != null ? String.valueOf(order.getDeletedBy()) : null)
                    .build();
        } catch (DataAccessException ex) {
            throw ManageException.dataAccess(ex);
        }
    }

    private orderMenuDtoV1 toOrderMenuDto(OrderMenu om) {
        return orderMenuDtoV1.builder()
                .menuId(om.getMenu() != null ? om.getMenu().getId().toString() : null)
                .menu(om.getMenu() != null ? om.getMenu().getName() : null)
                .amount(om.getOrderMenuQty())
                .price(om.getMenu() != null ? om.getMenu().getPrice() : 0)
                .build();
    }
}
