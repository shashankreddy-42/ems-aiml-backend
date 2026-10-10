package com.translator.repository;

import com.translator.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findAllByUserEmailOrderByCreatedAtDesc(String email);
    Optional<Translation> findByIdAndUserEmail(Long id, String email);
}
