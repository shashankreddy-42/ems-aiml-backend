package com.translator.repository;

import com.translator.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Favorite> findByUserEmailAndTranslationId(String email, Long translationId);
    Optional<Favorite> findByIdAndUserEmail(Long id, String email);
    void deleteAllByTranslationIdAndUserEmail(Long translationId, String email);
    long countByUserEmail(String email);
}
