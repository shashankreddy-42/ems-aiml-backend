package com.translator.dto;

import com.translator.entity.Favorite;
import java.time.Instant;

public record FavoriteResponse(Long id, Long translationId, String sourceLanguage, String targetLanguage,
        String sourceText, String translatedText, Instant savedAt) {
    public static FavoriteResponse from(Favorite f) {
        var t = f.getTranslation();
        return new FavoriteResponse(f.getId(), t.getId(), t.getSourceLanguage(), t.getTargetLanguage(),
                t.getSourceText(), t.getTranslatedText(), f.getCreatedAt());
    }
}
