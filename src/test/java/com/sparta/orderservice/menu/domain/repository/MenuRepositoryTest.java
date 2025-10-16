package com.sparta.orderservice.menu.domain.repository;

import com.sparta.orderservice.global.infrastructure.auditing.JpaAuditingConfig;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
@DisplayName("메뉴 레포지토리")
public class MenuRepositoryTest {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    TestEntityManager manager;

    @BeforeEach
    void setUp() {
        UUID storeId = UUID.randomUUID();
        manager.persist(new MenuEntity(null, "자장면", "자장 자장 자장면", 9000, true, storeId));
        manager.persist(new MenuEntity(null, "짬뽕", "신라면 정도 매움", 10000, true, storeId));
        manager.persist(new MenuEntity(null, "탕수육", "찹쌀 탕수육", 12000, true, storeId));
        manager.persist(new MenuEntity(null, "팔보채", "", 12000, true, storeId));
        manager.persist(new MenuEntity(null, "계란볶음밥", "낫알이 살아있는 볶음밥", 8500, true, storeId));
        manager.persist(new MenuEntity(null, "우육면", "깔끔한 육수", 10000, false, storeId));
        manager.flush();
    }

    @Test
    @DisplayName("메뉴 생성, 상세")
    void testCreateMenu() throws Exception {
        //before
        UUID storeId = UUID.randomUUID();
        MenuEntity menuEntity = MenuEntity.builder()
                .name("피자")
                .description("맛남")
                .price(10000)
                .isPublic(true)
                .storeId(storeId)
                .build();

        //when
        MenuEntity savedMenu = menuRepository.save(menuEntity);
        MenuEntity foundMenu = menuRepository.findById(savedMenu.getId()).orElseThrow();

        // then
        assertEquals("피자", savedMenu.getName());
        assertEquals("맛남", savedMenu.getDescription());
        assertEquals(10000, savedMenu.getPrice());
        assertTrue(savedMenu.isPublic());
        assertEquals(savedMenu.getId(), foundMenu.getId());
        assertEquals(savedMenu.getName(), foundMenu.getName());
        assertEquals(savedMenu.getDescription(), foundMenu.getDescription());
        assertEquals(savedMenu.getPrice(), foundMenu.getPrice());
        assertEquals(savedMenu.isPublic(), foundMenu.isPublic());
//        assertNotNull(foundMenu.getCreatedBy());
//        assertNotNull(foundMenu.getCreatedAt());

        System.out.println("생성자 : " + savedMenu.getCreatedBy() + "\n생성일 : " + savedMenu.getCreatedAt());
    }

    @Test
    @DisplayName("메뉴 조회")
    public void testFindAllByStoreId() {
        //build
        //가게 아이디

        //when
//        MenuEntity foundMenus = menuRepository.findAllByStoreId(가게 아이디);

        //then

    }

    @Test
    @DisplayName("메뉴 상세")
    public void testFindById() {

        //build
        MenuEntity menu = menuRepository.findByName("짬뽕").orElseThrow(() -> new NullPointerException());

        //when
        Optional<MenuEntity> foundMenu = menuRepository.findById(menu.getId());

        //then
        assertTrue(foundMenu.isPresent());
    }

    @Test
    @DisplayName("메뉴 수정")
    public void testUpdateMenu() {
    }

    @Test
    @DisplayName("메뉴 삭제")
    public void testDeleteMenu() throws Exception {

        //build
        MenuEntity foundMenu = menuRepository.findByName("짬뽕").orElseThrow(() -> new NullPointerException());

        //when
        menuRepository.delete(foundMenu);
        Optional<MenuEntity> deletedMenu = menuRepository.findById(foundMenu.getId());

        //then
        assertFalse(deletedMenu.isPresent());
    }
}
