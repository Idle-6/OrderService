package com.sparta.orderservice.order.domain.entity;

import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Table(name = "p_order")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_id", updatable = false, nullable = false)
    private UUID orderId;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(columnDefinition = "text")
    private String orderMessage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
    private Long updatedBy;

    private LocalDateTime deletedAt;
    private Long deletedBy;

    // 관계 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private Order(User user, Store store, Integer totalPrice, String orderMessage, User createdBy) {
        this.user = user;
        this.store = store;
        this.totalPrice = totalPrice;
        this.orderMessage = orderMessage;
        this.orderStatus = OrderStatus.CREATED;
        this.createdBy = createdBy;
    }

    public static Order ofNewOrder(User user, Store store, Integer totalPrice, String orderMessage, User createdBy) {
        return new Order(user, store, totalPrice, orderMessage, createdBy);
    }

    public void updateOrderStatus(OrderStatus orderStatus, Long updatedBy) {
        this.orderStatus = orderStatus;
        this.updatedBy = updatedBy;
    }

    public void cancelOrder(Long updatedBy) {
        this.orderStatus = OrderStatus.CANCELED;
        this.updatedBy = updatedBy;
    }
}


