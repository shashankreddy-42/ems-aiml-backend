package com.translator.controller;

import com.translator.dto.ProfileResponse;
import com.translator.dto.ProfileUpdateRequest;
import com.translator.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
public class UserController {
    private final UserService service;
    public UserController(UserService service) { this.service = service; }
    @GetMapping public ProfileResponse get(Authentication auth) { return service.profile(auth.getName()); }
    @PutMapping public ProfileResponse update(@Valid @RequestBody ProfileUpdateRequest request, Authentication auth) {
        return service.update(auth.getName(), request);
    }
}
