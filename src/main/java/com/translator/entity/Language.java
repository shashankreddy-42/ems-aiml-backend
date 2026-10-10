package com.translator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "languages", uniqueConstraints = @UniqueConstraint(name = "uk_lang_code", columnNames = "language_code"))
public class Language {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "language_code", nullable = false, length = 10)
    private String languageCode;

    @Column(name = "language_name", nullable = false, length = 80)
    private String languageName;

    @Column(name = "native_name", nullable = false, length = 80)
    private String nativeName;

    @Column(name = "translation_supported", nullable = false)
    private boolean translationSupported = true;

    @Column(name = "speech_supported", nullable = false)
    private boolean speechSupported;

    @Column(name = "tts_supported", nullable = false)
    private boolean ttsSupported;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLanguageCode() { return languageCode; }
    public void setLanguageCode(String languageCode) { this.languageCode = languageCode; }
    public String getLanguageName() { return languageName; }
    public void setLanguageName(String languageName) { this.languageName = languageName; }
    public String getNativeName() { return nativeName; }
    public void setNativeName(String nativeName) { this.nativeName = nativeName; }
    public boolean isTranslationSupported() { return translationSupported; }
    public void setTranslationSupported(boolean translationSupported) { this.translationSupported = translationSupported; }
    public boolean isSpeechSupported() { return speechSupported; }
    public void setSpeechSupported(boolean speechSupported) { this.speechSupported = speechSupported; }
    public boolean isTtsSupported() { return ttsSupported; }
    public void setTtsSupported(boolean ttsSupported) { this.ttsSupported = ttsSupported; }
}
