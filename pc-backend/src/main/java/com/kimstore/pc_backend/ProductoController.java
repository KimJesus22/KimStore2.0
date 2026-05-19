package com.kimstore.pc_backend;

import com.kimstore.pc_backend.service.ProductoService;
import com.kimstore.pc_backend.dto.ProductoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Para que Astro pueda hablar con Java sin problemas
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public List<Producto> listar() {
        return productoService.obtenerTodos();
    }

    @PostMapping
    public Producto crear(@Valid @RequestBody ProductoDTO producto) {
        return productoService.guardar(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id, @Valid @RequestBody ProductoDTO producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
