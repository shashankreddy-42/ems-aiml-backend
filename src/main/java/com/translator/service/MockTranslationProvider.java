package com.translator.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/** Explicit development-only provider. It does not pretend to translate text. */
@Component
@ConditionalOnProperty(name = "translation.provider", havingValue = "mock", matchIfMissing = true)
public class MockTranslationProvider implements TranslationProvider {
    @Override public String translate(String text, String sourceLanguage, String targetLanguage) {
        throw new IllegalStateException("Translation is not configured. Set TRANSLATION_PROVIDER=libretranslate and configure its URL.");
    }
    @Override public LanguageDetection detectLanguage(String text) {
        throw new IllegalStateException("Language detection is not configured. Set TRANSLATION_PROVIDER=libretranslate and configure its URL.");
    }
}
