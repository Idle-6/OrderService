package com.sparta.orderservice.menu.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.orderservice.auth.infrastructure.util.JwtProperties;
import com.sparta.orderservice.auth.infrastructure.util.JwtUtil;
import com.sparta.orderservice.global.infrastructure.config.SecurityConfig;
import com.sparta.orderservice.global.infrastructure.config.auditing.JpaAuditingConfig;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.global.infrastructure.security.UserDetailsServiceImpl;
import com.sparta.orderservice.menu.domain.entity.MenuEntity;
import com.sparta.orderservice.payment.domain.repository.impl.CustomPaymentRepositoryImpl;
import com.sparta.orderservice.user.domain.entity.User;
import com.sparta.orderservice.user.domain.entity.UserRoleEnum;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = SecurityConfig.class
            )
        },
        excludeAutoConfiguration = CustomPaymentRepositoryImpl.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JwtUtil.class, JwtProperties.class, UserDetailsServiceImpl.class, JpaAuditingConfig.class})
@DisplayName("메뉴 레포지토리")
public class MenuRepositoryTest {

    private Principal mockPrincipal;

    private UUID storeId;

    @MockitoBean
    private JPAQueryFactory jpaQueryFactory;

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

        //mock HttpRequest, mock principal 구성
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(mockPrincipal);
        SecurityContextHolder.getContext()
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());

        //테스트 데이터 생성
        this.storeId = UUID.randomUUID();
        MenuEntity deletedMenu = MenuEntity.builder()
                .name("나가사키 짬뽕")
                .isPublic(false)
                .storeId(storeId)
                .deletedAt(LocalDateTime.now())
                .build();
//        User user = User.builder().email("user1@test.com").password("password1").name("김철수").address("서울 강남구").role(UserRoleEnum.USER).isActive(true).build();
//        Category category = Category.ofNewCategory("한식", user.getUserId());
//        Store storeId = Store.ofNewStore("가게이름1", "123-45-67890", "010-1111-1111", "서울 강남구 역삼동", "맛있는 한식", true, category, user);
        manager.persist(new MenuEntity(null, "자장면", "자장 자장 자장면", 9000, true, storeId));
        manager.persist(new MenuEntity(null, "짬뽕", "신라면 정도 매움", 10000, true, storeId));
        manager.persist(new MenuEntity(null, "해물짬뽕", "", 11000, true, storeId));
        manager.persist(new MenuEntity(null, "차돌 짬뽕", "", 11000, true, storeId));
        manager.persist(new MenuEntity(null, "짬뽕 곱빼기", "", 13000, true, storeId));
        manager.persist(new MenuEntity(null, "탕수육", "찹쌀 탕수육", 12000, true, storeId));
        manager.persist(new MenuEntity(null, "팔보채", "", 12000, true, storeId));
        manager.persist(new MenuEntity(null, "계란볶음밥", "낫알이 살아있는 볶음밥", 8500, true, storeId));
        manager.persist(new MenuEntity(null, "우육면", "깔끔한 육수", 10000, false, storeId));
        manager.persist(deletedMenu);
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
        assertNotNull(foundMenu.getCreatedAt());
        assertNotNull(foundMenu.getUpdatedAt());
        assertEquals(1L, foundMenu.getCreatedBy());
        assertEquals(1L, foundMenu.getUpdatedBy());
        assertNull(foundMenu.getDeletedAt());
        assertNull(foundMenu.getDeletedBy());
    }

    @Test
    @DisplayName("메뉴 리스트 조회 - USER")
    public void testUserFindAllByStoreId() {
        //before
        String search = "%%";
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = false;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Page<MenuEntity> foundMenus = menuRepository.findAllByStoreIdAndIsPublicIsTrueAndDeletedAtNullAndNameLike(storeId, search, pageable);

        //then : 공개, 삭제x, storeId
        assertEquals(8, foundMenus.getTotalElements());
    }

    @Test
    @DisplayName("메뉴 리스트 조회, 검색 - USER")
    public void testUserFindAllByStoreIdSearch() {
        //before
        String search = "%짬뽕%";
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = false;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Page<MenuEntity> foundMenus = menuRepository.findAllByStoreIdAndIsPublicIsTrueAndDeletedAtNullAndNameLike(storeId, search, pageable);

        //then : 공개, 삭제x, 검색, storeId
        assertEquals(4, foundMenus.getTotalElements());

    }

    @Test
    @DisplayName("메뉴 리스트 조회, 정렬 - USER")
    public void testUserFindAllByStoreIdSort() {
        //before
        String search = "%%";
        int page = 0;
        int size = 10;
        String sortBy = "name";
        boolean isAsc = true;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Page<MenuEntity> foundMenus = menuRepository.findAllByStoreIdAndIsPublicIsTrueAndDeletedAtNullAndNameLike(storeId, search, pageable);

        //then : 공개, 삭제x, storeId
        assertEquals(8, foundMenus.getTotalElements());
        assertEquals("계란볶음밥", foundMenus.getContent().get(0).getName());
        assertEquals("자장면", foundMenus.getContent().get(1).getName());
        assertEquals("짬뽕", foundMenus.getContent().get(2).getName());
        assertEquals("짬뽕 곱빼기", foundMenus.getContent().get(3).getName());
        assertEquals("차돌 짬뽕", foundMenus.getContent().get(4).getName());
        assertEquals("탕수육", foundMenus.getContent().get(5).getName());
        assertEquals("팔보채", foundMenus.getContent().get(6).getName());
        assertEquals("해물짬뽕", foundMenus.getContent().get(7).getName());
    }

    @Test
    @DisplayName("메뉴 리스트 조회 - ADMIN")
    public void testAdminFindAllByStoreId() {
        //before
        String username = "sollertia4351";
        String password = "robbie1234";
        String email = "sollertia@sparta.com";
        UserRoleEnum role = UserRoleEnum.ADMIN;
        User testUser = User.builder()
                .userId(1L)
                .name(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());

        //mock HttpRequest, mock principal 구성
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(mockPrincipal);
        SecurityContextHolder.getContext()
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());

        String search = "%%";
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = false;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Page<MenuEntity> foundMenus = menuRepository.findAllByStoreIdAndDeletedAtNullAndNameLike(storeId, search, pageable);

        //then : 공개, 비공개, 삭제x, storeId
        assertEquals(9, foundMenus.getTotalElements());
    }

    @Test
    @DisplayName("메뉴 리스트 조회, 검색 - ADMIN")
    public void testAdminFindAllByStoreIdSearch() {
        //before
        String username = "sollertia4351";
        String password = "robbie1234";
        String email = "sollertia@sparta.com";
        UserRoleEnum role = UserRoleEnum.ADMIN;
        User testUser = User.builder()
                .userId(1L)
                .name(username)
                .password(password)
                .email(email)
                .role(role)
                .build();
        UserDetailsImpl testUserDetails = new UserDetailsImpl(testUser);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());

        //mock HttpRequest, mock principal 구성
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setUserPrincipal(mockPrincipal);
        SecurityContextHolder.getContext()
                .setAuthentication((Authentication) ((HttpServletRequest) req).getUserPrincipal());

        String search = "%짬뽕%";
        int page = 1;
        int size = 10;
        String sortBy = "createdAt";
        boolean isAsc = false;
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        //when
        Page<MenuEntity> foundMenus = menuRepository.findAllByStoreIdAndDeletedAtNullAndNameLike(storeId, search, pageable);

        //then : 공개, 비공개, 삭제x, storeId
        assertEquals(4, foundMenus.getTotalElements());
    }

    @Test
    @DisplayName("메뉴 상세 조회")
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
        foundMenu.delete();
        manager.flush();
        Optional<MenuEntity> deletedMenu = menuRepository.findById(foundMenu.getId());

        //then
        assertTrue(deletedMenu.isPresent());
        assertNotNull(deletedMenu.get().getDeletedAt());
        assertEquals(1L, deletedMenu.get().getDeletedBy());
    }
}
