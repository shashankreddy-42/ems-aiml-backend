package com.translator.dto;

public record ProfileResponse(UserResponse user, String defaultSourceLanguage,
        String defaultTargetLanguage, String theme, long translationsCompleted,
        long favoriteTranslations, long voiceTranslations, long languagesUsed) { }
