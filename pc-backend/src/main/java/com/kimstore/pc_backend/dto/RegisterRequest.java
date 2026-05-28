package com.kimstore.pc_backend.dto;

public record RegisterRequest(
        String email,
        String password
) {
}
