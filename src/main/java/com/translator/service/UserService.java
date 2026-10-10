package com.translator.service;

import com.translator.dto.ProfileResponse;
import com.translator.dto.ProfileUpdateRequest;
import com.translator.dto.UserResponse;
import com.translator.entity.UserPreference;
import com.translator.exception.ResourceNotFoundException;
import com.translator.repository.FavoriteRepository;
import com.translator.repository.LanguageRepository;
import com.translator.repository.TranslationRepository;
import com.translator.repository.UserPreferenceRepository;
import com.translator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;

@Service
public class UserService {
    private final UserRepository users;
    private final UserPreferenceRepository preferences;
    private final TranslationRepository translations;
    private final FavoriteRepository favorites;
    private final LanguageRepository languages;

    public UserService(UserRepository users, UserPreferenceRepository preferences,
            TranslationRepository translations, FavoriteRepository favorites, LanguageRepository languages) {
        this.users = users; this.preferences = preferences; this.translations = translations;
        this.favorites = favorites; this.languages = languages;
    }

    @Transactional(readOnly = true)
    public ProfileResponse profile(String email) {
        var user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var pref = preferences.findByUserId(user.getId()).orElseGet(() -> defaults(user));
        var items = translations.findAllByUserEmailOrderByCreatedAtDesc(email);
        var used = new HashSet<String>();
        items.forEach(t -> { used.add(t.getSourceLanguage()); used.add(t.getTargetLanguage()); });
        return new ProfileResponse(UserResponse.from(user), pref.getDefaultSourceLanguage(),
                pref.getDefaultTargetLanguage(), pref.getTheme(), items.size(),
                favorites.countByUserEmail(email), items.stream().filter(t -> t.isVoiceInput()).count(), used.size());
    }

    @Transactional
    public ProfileResponse update(String email, ProfileUpdateRequest request) {
        var user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        for (String code : new String[]{request.preferredLanguage(), request.defaultSourceLanguage(), request.defaultTargetLanguage()}) {
            if (!languages.existsByLanguageCode(code)) throw new IllegalArgumentException("Choose a supported language");
        }
        if (request.defaultSourceLanguage().equals(request.defaultTargetLanguage()))
            throw new IllegalArgumentException("Default source and target languages must be different");
        user.setName(request.name().trim());
        user.setPreferredLanguage(request.preferredLanguage());
        users.save(user);
        var pref = preferences.findByUserId(user.getId()).orElseGet(() -> defaults(user));
        pref.setDefaultSourceLanguage(request.defaultSourceLanguage());
        pref.setDefaultTargetLanguage(request.defaultTargetLanguage());
        pref.setTheme(request.theme());
        preferences.save(pref);
        return profile(email);
    }

    private UserPreference defaults(com.translator.entity.User user) {
        var pref = new UserPreference();
        pref.setUser(user);
        return pref;
    }
}
