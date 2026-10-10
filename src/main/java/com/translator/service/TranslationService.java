package com.translator.service;

import com.translator.dto.TranslationRequest;
import com.translator.dto.TranslationResponse;
import com.translator.dto.LanguageDetectionResponse;
import com.translator.entity.Translation;
import com.translator.entity.User;
import com.translator.exception.ResourceNotFoundException;
import com.translator.repository.LanguageRepository;
import com.translator.repository.FavoriteRepository;
import com.translator.repository.TranslationRepository;
import com.translator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TranslationService {
    private final TranslationProvider provider;
    private final TranslationRepository translations;
    private final UserRepository users;
    private final LanguageRepository languages;
    private final FavoriteRepository favorites;

    public TranslationService(TranslationProvider provider, TranslationRepository translations,
            UserRepository users, LanguageRepository languages, FavoriteRepository favorites) {
        this.provider = provider; this.translations = translations; this.users = users;
        this.languages = languages; this.favorites = favorites;
    }

    @Transactional
    public TranslationResponse translate(String email, TranslationRequest req) {
        String source = req.sourceLanguage().trim();
        String target = req.targetLanguage().trim();
        if (source.equalsIgnoreCase(target)) throw new IllegalArgumentException("Choose two different languages");
        if (!languages.existsByLanguageCode(source) || !languages.existsByLanguageCode(target))
            throw new IllegalArgumentException("Select languages from the supported language list");
        String result = provider.translate(req.text().trim(), source, target);
        User user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Translation t = new Translation();
        t.setUser(user); t.setSourceLanguage(source); t.setTargetLanguage(target);
        t.setSourceText(req.text().trim()); t.setTranslatedText(result); t.setVoiceInput(req.voiceInput());
        return TranslationResponse.from(translations.save(t));
    }

    @Transactional(readOnly = true)
    public LanguageDetectionResponse detect(String text) {
        var detected = provider.detectLanguage(text.trim());
        var language = languages.findByLanguageCode(detected.languageCode())
                .orElse(null);
        return new LanguageDetectionResponse(detected.languageCode(),
                language == null ? detected.languageCode() : language.getLanguageName(),
                language == null ? detected.languageCode() : language.getNativeName(), detected.confidence());
    }

    @Transactional(readOnly = true)
    public List<TranslationResponse> history(String email) {
        return translations.findAllByUserEmailOrderByCreatedAtDesc(email).stream().map(TranslationResponse::from).toList();
    }

    @Transactional
    public void delete(String email, Long id) {
        Translation translation = translations.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found"));
        favorites.deleteAllByTranslationIdAndUserEmail(id, email);
        translations.delete(translation);
    }
}
