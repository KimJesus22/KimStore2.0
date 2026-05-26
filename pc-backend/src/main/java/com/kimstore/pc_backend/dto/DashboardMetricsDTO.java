package com.kimstore.pc_backend.dto;

public record DashboardMetricsDTO(
    long totalProductos,
    double valorTotalInventario,
    long cantidadStockBajo
) {}
