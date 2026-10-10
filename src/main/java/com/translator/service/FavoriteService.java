package com.translator.service;

import com.translator.dto.FavoriteResponse;
import com.translator.entity.Favorite;
import com.translator.exception.ResourceNotFoundException;
import com.translator.repository.FavoriteRepository;
import com.translator.repository.TranslationRepository;
import com.translator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class FavoriteService {
    private final FavoriteRepository favorites;
    private final TranslationRepository translations;
    private final UserRepository users;
    public FavoriteService(FavoriteRepository favorites, TranslationRepository translations, UserRepository users) {
        this.favorites = favorites; this.translations = translations; this.users = users;
    }
    @Transactional(readOnly = true)
    public List<FavoriteResponse> list(String email) {
        return favorites.findAllByUserEmailOrderByCreatedAtDesc(email).stream().map(FavoriteResponse::from).toList();
    }
    @Transactional
    public FavoriteResponse add(String email, Long translationId) {
        var user = users.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        var translation = translations.findByIdAndUserEmail(translationId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Translation not found"));
        var favorite = favorites.findByUserEmailAndTranslationId(email, translationId).orElseGet(() -> {
            Favorite f = new Favorite(); f.setUser(user); f.setTranslation(translation); return favorites.save(f);
        });
        return FavoriteResponse.from(favorite);
    }
    @Transactional
    public void delete(String email, Long id) {
        var f = favorites.findByIdAndUserEmail(id, email).orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));
        favorites.delete(f);
    }
}
