package com.kimstore.pc_backend.dto;

public record ItemPedidoDTO(
    String nombreProducto,
    int cantidad,
    double precioUnitario
) {}
