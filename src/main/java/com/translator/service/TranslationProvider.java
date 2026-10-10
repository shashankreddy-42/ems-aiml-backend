package com.translator.service;

public interface TranslationProvider {
    String translate(String text, String sourceLanguage, String targetLanguage);
    LanguageDetection detectLanguage(String text);
    record LanguageDetection(String languageCode, Double confidence) { }
}
