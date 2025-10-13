package com.sparta.orderservice.menu.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuCreateDtoV1;
import com.sparta.orderservice.menu.presentation.dto.request.ReqMenuUpdateDtoV1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuControllerV1.class)
public class MenuControllerTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    @DisplayName("메뉴 생성")
    public void testCreateMenu() throws Exception {
        //build
        ReqMenuCreateDtoV1 reqDto = ReqMenuCreateDtoV1.builder()
                .name("치킨")
                .description("맛남")
                .price(12000)
                .isPublic(true)
                .isUseAi(false)
                .prompt("")
                .build();

        String requestBody = objectMapper.writeValueAsString(reqDto);

        //when-then
        mvc.perform(post("/menus")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("치킨"))
                .andExpect(jsonPath("$.description").value("맛남"))
                .andExpect(jsonPath("$.price").value(12000))
                .andExpect(jsonPath("$.is_public").value(true))
                .andDo(print());
    }

    @Test
    @DisplayName("메뉴 조회")
    public void testGetMenuListByStoreId() throws Exception {
        //build
        UUID storeId = UUID.randomUUID();
        MultiValueMap<String, String> reqParams = new LinkedMultiValueMap<>();
        reqParams.add("page", "1");
        reqParams.add("size", "10");
        reqParams.add("sortBy", "createdAt");
        reqParams.add("isAsc", "true");

        //when-then
        mvc.perform(get("/menus/" + storeId + "/menu")
                .params(reqParams)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numberOfElements").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("메뉴 상세")
    public void testGetMenuById() throws Exception {
        //build
        UUID menuId = UUID.randomUUID();

        //when-then
        mvc.perform(get("/menus/" + menuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(menuId.toString()))
                .andDo(print());
    }

    @Test
    @DisplayName("메뉴 수정")
    public void testUpdateMenu() throws Exception {
        //build
        UUID menuId = UUID.randomUUID();
        ReqMenuUpdateDtoV1 reqDto = ReqMenuUpdateDtoV1.builder()
                .name("치킨")
                .description("맛남")
                .price(12000)
                .isPublic(true)
                .build();

        //when-then
        mvc.perform(patch("/menus/" + menuId)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("메뉴 삭제")
    public void testDeleteMenu() throws Exception {
        //build
        UUID menuId = UUID.randomUUID();

        //when-then
        mvc.perform(delete("/menus/" + menuId))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
