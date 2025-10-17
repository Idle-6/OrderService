package com.sparta.orderservice.store.domain.repository;

import com.sparta.orderservice.category.domain.entity.Category;
import com.sparta.orderservice.store.domain.entity.Store;
import com.sparta.orderservice.store.presentation.dto.SearchParam;
import com.sparta.orderservice.store.presentation.dto.request.ReqStoreUpdateDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDetailDtoV1;
import com.sparta.orderservice.store.presentation.dto.response.ResStoreDtoV1;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@DataJpaTest
class StoreRepositoryTest {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    TestEntityManager manager;

    User user1;
    Category category1, category2;
    Store store1;
    @BeforeEach
    void setUp() {
        User admin = User.builder().email("admin@test.com").password("admin1").name("관리자").address("서울 강남구").role(UserRoleEnum.ADMIN).isActive(true).build();
        user1 = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
        User user2 = User.builder().email("user2@test.com").password("password2").name("이영희").address("서울 마포구").role(UserRoleEnum.USER).isActive(true).build();
        User user3 = User.builder().email("user3@test.com").password("password3").name("박민수").address("서울 서초구").role(UserRoleEnum.USER).isActive(true).build();
        User user4 = User.builder().email("user4@test.com").password("password4").name("최지우").address("서울 송파구").role(UserRoleEnum.USER).isActive(true).build();
        User user5 = User.builder().email("user5@test.com").password("password5").name("정다은").address("서울 강서구").role(UserRoleEnum.USER).isActive(true).build();
        manager.persist(admin);
        manager.persist(user1);
        manager.persist(user2);
        manager.persist(user3);
        manager.persist(user4);
        manager.persist(user5);

        category1 = Category.ofNewCategory("한식", admin.getUserId());
        category2 = Category.ofNewCategory("중식", admin.getUserId());
        manager.persist(category1);
        manager.persist(category2);
        manager.flush();

        store1 = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category1, user1);
        Store store2 = Store.ofNewStore("가게이름2", "223-45-67890", "010-2222-2222", "서울 마포구 연남동", "시원한 한식", false, category1, user2);
        Store store3 = Store.ofNewStore("가게이름3", "323-45-67890", "010-3333-3333", "서울 서초구 서초동", "중식", true, category2, user3);
        Store store4 = Store.ofNewStore("가게이름4", "423-45-67890", "010-4444-4444", "서울 송파구 가락동", "전통 한식", true, category1, user4);
        Store store5 = Store.ofNewStore("가게이름5", "523-45-67890", "010-5555-5555", "서울 강서구 등촌동", "모던 한식", true, category1, user5);

        manager.persistAndFlush(store1);
        manager.persistAndFlush(store2);
        manager.persistAndFlush(store3);
        manager.persistAndFlush(store4);
        manager.persistAndFlush(store5);
    }

    @Test
    @DisplayName("가게 수정")
    void update() {
        ReqStoreUpdateDtoV1 request = new ReqStoreUpdateDtoV1(category1.getCategoryId(), "한식집", "123-45-67890", "010-1111-1111", "서울 마포구 연남동", "한식", true);
        store1.update(request, category1, user1.getUserId());

        manager.flush();

        assertNotNull(store1.getUpdatedAt());
        assertEquals(user1.getUserId(), store1.getUpdatedBy());
    }

    @Test
    @DisplayName("가게 삭제")
    void delete() {
        store1.delete(user1.getUserId());

        manager.flush();

        assertNotNull(store1.getDeletedAt());
        assertEquals(user1.getUserId(), store1.getDeletedBy());
    }

    @Test
    @DisplayName("가게 리스트 조회 - 사용자")
    void findStorePage() {
        SearchParam searchParam = new SearchParam();
        Page<ResStoreDtoV1> response = storeRepository.findStorePage(searchParam, Pageable.ofSize(5), false);

        assertFalse(response.isEmpty());

        assertAll(() -> {
           assertEquals(4, response.getTotalElements());
            assertEquals("모던 한식", response.getContent().get(0).getDescription());
            assertEquals("전통 한식", response.getContent().get(1).getDescription());
            assertEquals("중식", response.getContent().get(2).getDescription());
            assertEquals("맛있는 한식", response.getContent().get(3).getDescription());
        });

    }

    @Test
    @DisplayName("가게 리스트 조회 - 관리자")
    void findStorePage_manager() {
        SearchParam searchParam = new SearchParam();
        Page<ResStoreDtoV1> response = storeRepository.findStorePage(searchParam, Pageable.ofSize(5), true);

        assertFalse(response.isEmpty());

        assertAll(() -> {
            assertEquals(5, response.getTotalElements());
            assertEquals("모던 한식", response.getContent().get(0).getDescription());
            assertEquals("전통 한식", response.getContent().get(1).getDescription());
            assertEquals("중식", response.getContent().get(2).getDescription());
            assertEquals("시원한 한식", response.getContent().get(3).getDescription());
            assertEquals("맛있는 한식", response.getContent().get(4).getDescription());
        });
    }

    @Test
    @DisplayName("가게 리스트 조회 - 카테고리별")
    void findStorePage_categoryId() {
        SearchParam searchParam = new SearchParam(null, category2.getCategoryId());
        Page<ResStoreDtoV1> response = storeRepository.findStorePage(searchParam, Pageable.ofSize(5), false);

        assertFalse(response.isEmpty());

        assertAll(() -> {
            assertEquals(1, response.getTotalElements());
            assertEquals("중식", response.getContent().get(0).getDescription());
        });
    }

    @Test
    @DisplayName("가게 리스트 조회 - 검색")
    void findStorePage_search() {
        SearchParam searchParam = new SearchParam("한식", null);
        Page<ResStoreDtoV1> response = storeRepository.findStorePage(searchParam, Pageable.ofSize(5), false);

        assertFalse(response.isEmpty());

        assertAll(() -> {
            assertEquals(3, response.getTotalElements());
            assertEquals("모던 한식", response.getContent().get(0).getDescription());
            assertEquals("전통 한식", response.getContent().get(1).getDescription());
            assertEquals("맛있는 한식", response.getContent().get(2).getDescription());
        });

    }

    @Test
    @DisplayName("가게 상세 조회 - storeId")
    void findStoreDetailById() {
        Optional<ResStoreDetailDtoV1> response = storeRepository.findStoreDetailById(store1.getStoreId());

        assertTrue(response.isPresent());

        assertAll(() -> {
            assertEquals("가게이름1", response.get().getName());
            assertEquals("123-45-67890", response.get().getBizRegNo());
            assertEquals("010-1111-1111", response.get().getContact());
            assertEquals("서울 강남구 역삼동", response.get().getAddress());
            assertEquals("맛있는 한식", response.get().getDescription());
        });
    }

    @Test
    @DisplayName("가게 상세 조회 - userId")
    void findStoreDetailByUserId() {
        Optional<ResStoreDetailDtoV1> response = storeRepository.findStoreDetailByUserId(user1.getUserId());

        assertTrue(response.isPresent());

        assertAll(() -> {
            assertEquals("가게이름1", response.get().getName());
            assertEquals("123-45-67890", response.get().getBizRegNo());
            assertEquals("010-1111-1111", response.get().getContact());
            assertEquals("서울 강남구 역삼동", response.get().getAddress());
            assertEquals("맛있는 한식", response.get().getDescription());
        });

    }
}