package com.sparta.orderservice.menu.infrastructure.api.gemini.client;

import com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response.ResGeminiDto;
import com.sparta.orderservice.menu.presentation.advice.exception.MenuException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiClient {

    private final RestClient restClient;

    public ResGeminiDto callApi(String prompt) {

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", new Object[] {
                            Map.of("parts", new Object[] {
                                    Map.of("text", prompt)
                            })
                    }
            );

            return restClient.post()
                    .uri("/v1beta/models/gemini-2.5-flash:generateContent")
                    .body(requestBody)
                    .retrieve()
                    .body(ResGeminiDto.class);

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
