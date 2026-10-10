package com.translator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Size(max = 10) String preferredLanguage,
        @NotBlank @Size(max = 10) String defaultSourceLanguage,
        @NotBlank @Size(max = 10) String defaultTargetLanguage,
        @NotBlank @Pattern(regexp = "light|dark") String theme) { }
