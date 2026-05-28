package com.kimstore.pc_backend.dto;

public record ResenaResponseDTO(
    Long id,
    String username,
    Integer calificacion,
    String comentario,
    String fecha
) {}
