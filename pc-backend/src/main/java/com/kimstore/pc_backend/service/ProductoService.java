package com.kimstore.pc_backend.service;

import com.kimstore.pc_backend.Producto;
import com.kimstore.pc_backend.ProductoRepository;
import com.kimstore.pc_backend.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // NUEVO MÉTODO DE BÚSQUEDA PAGINADA
    public Page<Producto> buscarPorNombrePagina(String texto, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (texto == null || texto.trim().isEmpty()) {
            // findAll() ya trae soporte nativo para PageRequest
            return productoRepository.findAll(pageRequest);
        }
        return productoRepository.findByNombreContainingIgnoreCase(texto, pageRequest);
    }

    public Producto guardar(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        producto.setImageUrl(dto.imageUrl());
        return productoRepository.save(producto);
    }

    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }

    public Producto actualizar(Long id, ProductoDTO dto) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(dto.nombre());
            producto.setDescripcion(dto.descripcion());
            producto.setPrecio(dto.precio());
            producto.setStock(dto.stock());
            producto.setImageUrl(dto.imageUrl());
            return productoRepository.save(producto);
        }).orElse(null);
    }
}
