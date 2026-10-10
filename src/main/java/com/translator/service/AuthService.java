package com.translator.service;

import com.translator.dto.*;
import com.translator.entity.User;
import com.translator.entity.UserPreference;
import com.translator.exception.DuplicateResourceException;
import com.translator.repository.UserPreferenceRepository;
import com.translator.repository.UserRepository;
import com.translator.security.CustomUserDetailsService;
import com.translator.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository users;
    private final UserPreferenceRepository prefs;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final CustomUserDetailsService uds;
    private final JwtService jwt;

    public AuthService(UserRepository users, UserPreferenceRepository prefs, PasswordEncoder encoder,
                       AuthenticationManager authManager, CustomUserDetailsService uds, JwtService jwt) {
        this.users = users; this.prefs = prefs; this.encoder = encoder;
        this.authManager = authManager; this.uds = uds; this.jwt = jwt;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (!req.password().equals(req.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }
        User u = new User();
        u.setName(req.name().trim());
        u.setEmail(email);
        u.setPassword(encoder.encode(req.password()));
        u = users.save(u);

        UserPreference p = new UserPreference();
        p.setUser(u);
        prefs.save(p);

        return buildResponse(u);
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.email().trim().toLowerCase();
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, req.password()));
        User u = users.findByEmail(email).orElseThrow();
        return buildResponse(u);
    }

    private AuthResponse buildResponse(User u) {
        UserDetails details = uds.loadUserByUsername(u.getEmail());
        return new AuthResponse(jwt.generateToken(details), "Bearer", jwt.getExpirationMs(), UserResponse.from(u));
    }
}
