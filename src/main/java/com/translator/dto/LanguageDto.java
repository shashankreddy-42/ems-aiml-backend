package com.translator.dto;

import com.translator.entity.Language;

public record LanguageDto(String code, String name, String nativeName,
                          boolean translationSupported, boolean speechSupported, boolean ttsSupported) {
    public static LanguageDto from(Language l) {
        return new LanguageDto(l.getLanguageCode(), l.getLanguageName(), l.getNativeName(),
                l.isTranslationSupported(), l.isSpeechSupported(), l.isTtsSupported());
    }
}
