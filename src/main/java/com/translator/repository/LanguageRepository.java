package com.translator.repository;

import com.translator.entity.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    List<Language> findAllByOrderByLanguageNameAsc();
    boolean existsByLanguageCode(String languageCode);
    Optional<Language> findByLanguageCode(String languageCode);
}
