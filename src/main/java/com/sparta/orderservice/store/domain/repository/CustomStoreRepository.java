package com.sparta.orderservice.store.domain.repository;

import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * {@code Store} 엔티티에 대한 커스텀 조회 기능을 제공하는 Repository 인터페이스입니다.
 * <p>
 * 기본 JPA Repository로 처리하기 어려운 복합 검색, 동적 조건 조회 등을
 * QueryDSL을 사용해 구현하기 위한 인터페이스입니다.
 * </p>
 */
public interface CustomStoreRepository {

    /**
     * 검색 조건과 페이지 정보를 기반으로
     * 가게 목록을 동적으로 조회합니다.
     *
     * @param searchParam 검색 조건을 담은 객체 (카테고리, 이름, 주소 등 포함)
     * @param pageable 페이지네이션 정보 (페이지 번호, 크기, 정렬 기준)
     * @return {@link Page} 형태의 가게 목록 결과
     */
    Page<ResStoreDtoV1> findStorePage(SearchParam searchParam, Pageable pageable);

    /**
     * 가게의 고유 ID(UUID)를 기준으로 상세 정보를 조회합니다.
     *
     * @param storeId 조회할 가게의 고유 식별자(UUID)
     * @return 해당 가게의 상세 정보 DTO,
     *         존재하지 않을 경우 {@link Optional#empty()} 반환
     */
    Optional<ResStoreDetailDtoV1> findStoreDetailById(UUID storeId);

    /**
     * 사용자 ID를 기준으로
     * 해당 사용자가 등록한 가게의 상세 정보를 조회합니다.
     *
     * @param userId 사용자 고유 식별자(Long)
     * @return 사용자가 등록한 가게의 상세 정보 DTO,
     *         존재하지 않을 경우 {@link Optional#empty()} 반환
     */
    Optional<ResStoreDetailDtoV1> findStoreDetailByUserId(Long userId);
}
