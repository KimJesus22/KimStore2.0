package com.kimstore.pc_backend.dto;

import java.util.List;

public record PedidoResponseDTO(
    Long id,
    String fecha,
    double total,
    List<ItemPedidoDTO> items
) {}
