package com.kimstore.pc_backend.dto;

public record ResenaRequestDTO(
    Long productoId,
    Integer calificacion,
    String comentario
) {}
