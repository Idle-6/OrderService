package com.sparta.orderservice.payment.domain.entity;

import com.sparta.orderservice.order.domain.entity.Order;
import com.sparta.orderservice.user.domain.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "p_payment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", updatable = false, nullable = false)
    private UUID paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethodEnum method;

    @Column(name = "payment_amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatusEnum status;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    private User user;

    private Long updatedBy;

    private Long deletedBy;

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    private Payment(PaymentMethodEnum method, BigDecimal amount, PaymentStatusEnum status, Order order, User user) {
        this.method = method;
        this.amount = amount;
        this.status = status;
        this.order = order;
        this.user = user;
    }

    public static Payment ofNewPayment(PaymentMethodEnum method, BigDecimal amount, PaymentStatusEnum status, Order order, User user) {
        return new Payment(method, amount, status, order, user);
    }

    public void updateStatus(PaymentStatusEnum status, Long updatedBy) {
        this.status = status;
        this.updatedBy = updatedBy;
    }

    public void cancel(Long deletedBy) {
        this.status = PaymentStatusEnum.CANCELED;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
