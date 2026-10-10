package com.translator.dto;

import com.translator.entity.Translation;
import java.time.Instant;

public record TranslationResponse(Long id, String sourceLanguage, String targetLanguage,
        String sourceText, String translatedText, boolean voiceInput, Instant createdAt) {
    public static TranslationResponse from(Translation t) {
        return new TranslationResponse(t.getId(), t.getSourceLanguage(), t.getTargetLanguage(),
                t.getSourceText(), t.getTranslatedText(), t.isVoiceInput(), t.getCreatedAt());
    }
}
