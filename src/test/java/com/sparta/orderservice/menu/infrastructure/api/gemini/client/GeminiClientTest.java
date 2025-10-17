package com.sparta.orderservice.menu.infrastructure.api.gemini.client;

import com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response.ResGeminiDto;
import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("제미나이")
class GeminiClientTest {

    @Test
    @DisplayName("AI API 호출")
    void testCallApi() throws Exception {
        //before
        String apiKey = "AIzaSyCajCwVkxfaWZ806nWIV3Ikzvs4LB878rM";
        String prompt = "만두 상품의 설명을 50자 이내로 추천해줘.";
        RestClient restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com")
                .defaultHeader("x-goog-api-key", apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        GeminiClient geminiClient = new GeminiClient(restClient);

        //when
        ResGeminiDto res = geminiClient.callApi(prompt);

        //then
        assertNotNull(res);
        assertNotNull(res.getResultText());
        System.out.println(
                "프롬프트 : " + prompt +
                "\n메뉴설명 : " + res.getResultText()
        );
    }
}