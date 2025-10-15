package com.sparta.orderservice.menu.infrastructure.api.gemini.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResGeminiDto {
    List<Candidate> candidates;
    UsageMetadata usageMetadata;
    String modelVersion;
    String responseId;

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
