package com.kimstore.pc_backend.dto;

import jakarta.validation.constraints.*;

public record ProductoDTO(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "La descripción es obligatoria")
        String descripcion,

        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero")
        Double precio,

        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        // Aquí NO ponemos el archivo real, solo el String por si queremos actualizarlo
        String imageUrl
) {}
