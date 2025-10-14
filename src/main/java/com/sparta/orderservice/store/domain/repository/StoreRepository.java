package com.sparta.orderservice.store.domain.repository;

import com.sparta.orderservice.store.domain.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Store, UUID>, CustomStoreRepository {
}
