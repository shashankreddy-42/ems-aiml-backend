package com.translator.service;

import com.translator.dto.LanguageDto;
import com.translator.repository.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageService {
    private final LanguageRepository repo;

    public LanguageService(LanguageRepository repo) { this.repo = repo; }

    // Phase 2 adds @EnableCaching + @Cacheable("languages") here.
    public List<LanguageDto> findAll() {
        return repo.findAllByOrderByLanguageNameAsc().stream().map(LanguageDto::from).toList();
    }
}
