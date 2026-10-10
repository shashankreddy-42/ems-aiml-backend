package com.translator.dto;

public record LanguageDetectionResponse(String languageCode, String languageName,
        String nativeName, Double confidence) { }
