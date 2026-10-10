package com.translator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConversationRequest(
        @NotBlank @Size(max = 10000) String text,
        @NotBlank @Size(max = 10) String sourceLanguage,
        @NotBlank @Size(max = 10) String targetLanguage,
        @NotBlank @Size(max = 40) String speakerLabel,
        boolean voiceInput) { }
