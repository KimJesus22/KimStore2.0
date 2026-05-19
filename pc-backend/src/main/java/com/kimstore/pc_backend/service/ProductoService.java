package com.kimstore.pc_backend.service;

import com.kimstore.pc_backend.Producto;
import com.kimstore.pc_backend.ProductoRepository;
import com.kimstore.pc_backend.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto guardar(ProductoDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.nombre());
        producto.setDescripcion(dto.descripcion());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
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
            return productoRepository.save(producto);
        }).orElse(null);
    }
}
