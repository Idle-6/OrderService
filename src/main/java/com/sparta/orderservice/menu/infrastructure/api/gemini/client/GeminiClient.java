package com.sparta.orderservice.menu.infrastructure.api.gemini.client;

import com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response.ResGeminiDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient restClient;

//    @Value("${gemini.api.key}")
//    private String apiKey;

//    public GeminiClient() {
//        this.restClient = RestClient.builder()
//                .baseUrl("https://generativelanguage.googleapis.com")
////                .defaultHeader("x-goog-api-key", apiKey)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .build();
//    }

//    @PostConstruct
//    public void init() {
//
//        if (apiKey == null || apiKey.isBlank()) {
//            throw new IllegalArgumentException("API 키 없음");
//        }
//
//        this.restClient = RestClient.builder()
//                .baseUrl("https://generativelanguage.googleapis.com")
//                .defaultHeader("x-goog-api-key", apiKey)
//                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//                .build();
//    }

    public ResGeminiDto callApi(String prompt) {

        Map<String, Object> requestBody = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[] {
                                Map.of("text", prompt)
                        })
                }
        );

        return restClient.post()
                .uri("/v1beta/models/gemini-2.5-flash:generateContent")
//                .header("x-goog-api-key", apiKey)
                .body(requestBody)
                .retrieve()
                .body(ResGeminiDto.class);
    }
}
