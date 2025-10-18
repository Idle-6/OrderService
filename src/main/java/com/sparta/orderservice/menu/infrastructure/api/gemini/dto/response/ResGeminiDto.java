package com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResGeminiDto {
    List<Candidate> candidates;
    UsageMetadata usageMetadata;
    String modelVersion;
    String responseId;

    public ResGeminiDto(String string) {
        this.candidates = new ArrayList<>();
        candidates.add(new Candidate());
        candidates.get(0).content = new Content();
        candidates.get(0).content.parts = List.of(new Part());
        candidates.get(0).content.parts.get(0).text = string;
    }

    @Getter
    private static class Candidate {
        Content content;
        String finishReason;
        int index;
    }

    @Getter
    private static class Content {
        List<Part> parts;
        String role;
    }

    @Getter
    private static class Part {
        String text;
    }

    @Getter
    private static class UsageMetadata {
        int promptTokenCount;
        int candidatesTokenCount;
        int totalTokenCount;
        List<PromptTokensDetail> promptTokensDetails;
        int thoughtsTokenCount;
    }

    @Getter
    private static class PromptTokensDetail {
        String modality;
        int tokenCount;
    }

    public String getResultText() {
        return this.candidates.get(0).content.parts.get(0).text;
    }
}
