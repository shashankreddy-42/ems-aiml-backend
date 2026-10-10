package com.translator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DetectLanguageRequest(@NotBlank @Size(max = 10000) String text) { }
