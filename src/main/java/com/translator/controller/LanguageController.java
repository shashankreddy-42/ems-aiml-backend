package com.translator.controller;

import com.translator.dto.LanguageDto;
import com.translator.service.LanguageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/languages")
public class LanguageController {
    private final LanguageService service;

    public LanguageController(LanguageService service) { this.service = service; }

    @GetMapping
    public List<LanguageDto> all() { return service.findAll(); }
}
