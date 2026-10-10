package com.translator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TranslationRequest(
        @NotBlank @Size(max = 10000) String text,
        @NotBlank @Size(max = 10) String sourceLanguage,
        @NotBlank @Size(max = 10) String targetLanguage,
        boolean voiceInput) { }
