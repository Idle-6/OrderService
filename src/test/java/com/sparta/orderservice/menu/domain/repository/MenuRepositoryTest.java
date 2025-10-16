package com.sparta.orderservice.menu.domain.repository;

import com.sparta.orderservice.auth.infrastructure.util.JwtProperties;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.auth.infrastructure.util.TokenBlacklistMemoryStore;
import com.sparta.orderservice.global.infrastructure.auditing.JpaAuditingConfig;
import com.sparta.orderservice.global.infrastructure.config.SecurityConfig;
import com.sparta.orderservice.global.infrastructure.config.auditing.AuditorAwareImpl;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsServiceImpl;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JwtUtil.class, JwtProperties.class, UserDetailsServiceImpl.class, TokenBlacklistMemoryStore.class, JpaAuditingConfig.class, SecurityConfig.class, AuditorAwareImpl.class})
@DisplayName("메뉴 레포지토리")
public class MenuRepositoryTest {

    private Principal mockPrincipal;

    private UUID storeId;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    TestEntityManager manager;

    @BeforeEach
    void setUp() {

        // Mock 테스트 유져 생성
        String username = "sollertia4351";
        String password = "robbie1234";
        String email = "sollertia@sparta.com";
        UserRoleEnum role = UserRoleEnum.USER;
        User testUser = User.builder()
                .userId(1L)
                .name(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());

//        MockHttpServletRequest req = Mockito.mock(MockHttpServletRequest.class);
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(mockPrincipal);
        SecurityContextHolder.getContext()
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());

        storeId = UUID.randomUUID();
        manager.persist(new MenuEntity(null, "자장면", "자장 자장 자장면", 9000, true, storeId));
        manager.persist(new MenuEntity(null, "짬뽕", "신라면 정도 매움", 10000, true, storeId));
        manager.persist(new MenuEntity(null, "탕수육", "찹쌀 탕수육", 12000, true, storeId));
        manager.persist(new MenuEntity(null, "팔보채", "", 12000, true, storeId));
        manager.persist(new MenuEntity(null, "계란볶음밥", "낫알이 살아있는 볶음밥", 8500, true, storeId));
        manager.persist(new MenuEntity(null, "우육면", "깔끔한 육수", 10000, false, storeId));
        manager.flush();
    }

    @AfterEach
    void tearDown() {
        manager.clear();
        SecurityContextHolder.clearContext();
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
        assertNotNull(foundMenu.getCreatedBy());
        assertNotNull(foundMenu.getCreatedAt());
        assertNotNull(foundMenu.getUpdatedAt());
        assertNotNull(foundMenu.getUpdatedBy());

        System.out.println("생성자 : " + savedMenu.getCreatedBy() + "\n생성일 : " + savedMenu.getCreatedAt());
    }

    @Test
    @DisplayName("메뉴 조회")
    public void testFindAllByStoreId() {
        //build
        //가게 아이디

        //when
//        MenuEntity foundMenus = menuRepository.findAllByStoreId(storeId);

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
    @Transactional
    public void testUpdateMenu() {

        MenuEntity foundMenu = menuRepository.findByName("짬뽕").orElseThrow(() -> new NullPointerException());
        LocalDateTime beforeUpdate = foundMenu.getUpdatedAt();

        foundMenu.setPrice(11000);
        manager.flush();
        MenuEntity updatedMenu = menuRepository.findById(foundMenu.getId()).orElseThrow();

        assertEquals(11000, updatedMenu.getPrice());
        assertNotSame(updatedMenu.getUpdatedAt(), beforeUpdate);

    }

    @Test
    @DisplayName("메뉴 삭제")
    public void testDeleteMenu() throws Exception {

        //build
        MenuEntity foundMenu = menuRepository.findByName("짬뽕").orElseThrow(() -> new NullPointerException());

        //when
//        menuRepository.delete(foundMenu);
        foundMenu.delete();
        menuRepository.save(foundMenu);
        Optional<MenuEntity> deletedMenu = menuRepository.findById(foundMenu.getId());

        //then
        assertTrue(deletedMenu.isPresent());
        assertNotNull(deletedMenu.get().getDeletedAt());
        assertNotNull(deletedMenu.get().getDeletedBy());
    }
}
