package com.sparta.orderservice.menu.domain.entity;

import com.sparta.orderservice.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "menu")
@Table(name = "menu")
public class MenuEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private String description;

    private int price;

    @Column(name="is_public", nullable = false)
    private boolean isPublic;

    @Column(name="store_id", nullable = false)
    private UUID storeId;

//    @ManyToOne
//    @Column(name = "store_id")
//    private StoreEntity store;
}
