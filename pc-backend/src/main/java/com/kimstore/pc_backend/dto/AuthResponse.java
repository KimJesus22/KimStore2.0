package com.kimstore.pc_backend.dto;

public record AuthResponse(
        String token,
        String role,
        String avatarUrl
) {
}
