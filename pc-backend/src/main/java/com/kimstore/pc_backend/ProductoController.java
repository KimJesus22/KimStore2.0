package com.kimstore.pc_backend;

import com.kimstore.pc_backend.service.ProductoService;
import com.kimstore.pc_backend.dto.ProductoDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Para que Astro pueda hablar con Java sin problemas
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public Page<Producto> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        return productoService.buscarPorNombrePagina(buscar, page, size);
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
