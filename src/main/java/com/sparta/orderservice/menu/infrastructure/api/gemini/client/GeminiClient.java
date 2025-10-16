package com.sparta.orderservice.menu.infrastructure.api.gemini.client;

import com.sparta.orderservice.global.infrastructure.security.UserDetailsImpl;
import com.sparta.orderservice.menu.infrastructure.api.gemini.domain.entity.AiLogEntity;
import com.sparta.orderservice.menu.infrastructure.api.gemini.domain.repository.AiRepository;
import com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response.ResGeminiDto;
import com.sparta.orderservice.menu.presentation.advice.exception.MenuException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient restClient;
    private final AiRepository aiRepository;

    public ResGeminiDto callApi(@AuthenticationPrincipal UserDetailsImpl userDetails, String prompt) {

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[] {
                            Map.of("parts", new Object[] {
                                    Map.of("text", prompt)
                            })
                    }
            );

            ResGeminiDto res = restClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent")
                    .body(requestBody)
                    .retrieve()
                    .body(ResGeminiDto.class);

            if(res == null) {
                throw MenuException.AiApiNullResponseException();
            }

            //TODO AI 로그 저장
            aiRepository.save(
                    AiLogEntity.builder()
                            .request(prompt)
                            .response(res.getResultText())
                            .createdAt(LocalDateTime.now())
                            .createdBy(userDetails.getUser().getUserId())
                            .build()
            );

            return res;

        } catch (HttpClientErrorException e) {
            throw MenuException.AiApiClientException(e);
        } catch (HttpServerErrorException e) {
            throw MenuException.AiApiServerException(e);
        } catch (ResourceAccessException e) {
            throw MenuException.AiApiNetworkException(e);
        } catch (RestClientException e) {
            throw MenuException.AiApiRestClientException(e);
        } catch (Exception e) {
            throw MenuException.AiApiUnknownException(e);
        }
    }
}
