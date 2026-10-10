package com.translator.controller;

import com.translator.dto.FavoriteResponse;
import com.translator.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    private final FavoriteService service;
    public FavoriteController(FavoriteService service) { this.service = service; }
    @GetMapping public List<FavoriteResponse> list(Authentication auth) { return service.list(auth.getName()); }
    @PostMapping("/{translationId}") @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponse add(@PathVariable Long translationId, Authentication auth) { return service.add(auth.getName(), translationId); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication auth) { service.delete(auth.getName(), id); }
}
