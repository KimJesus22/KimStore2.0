package com.kimstore.pc_backend.dto;

public record LoginRequest(
        String username,
        String password
) {
}
