package com.translator.dto;

import com.translator.entity.User;
import java.time.Instant;

/** Never contains the password. */
public record UserResponse(Long id, String name, String email, String preferredLanguage, Instant createdAt) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getPreferredLanguage(), u.getCreatedAt());
    }
}
