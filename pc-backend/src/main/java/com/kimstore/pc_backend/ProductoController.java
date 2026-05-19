package com.kimstore.pc_backend;

import com.kimstore.pc_backend.dto.ProductoDTO;
import com.kimstore.pc_backend.service.CloudinaryService;
import com.kimstore.pc_backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Para que Astro pueda hablar con Java sin problemas
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public Page<Producto> listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        return productoService.buscarPorNombrePagina(buscar, page, size);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public Producto crear(
            @Valid @RequestPart("producto") ProductoDTO productoDto,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        String imageUrl = productoDto.imageUrl();

        if (imagen != null && !imagen.isEmpty()) {
            imageUrl = cloudinaryService.subirImagen(imagen);
        }

        ProductoDTO productoConImagen = new ProductoDTO(
                productoDto.nombre(),
                productoDto.descripcion(),
                productoDto.precio(),
                productoDto.stock(),
                imageUrl
        );

        return productoService.guardar(productoConImagen);
    }

    @PutMapping(path = "/{id}", consumes = {"multipart/form-data"})
    public Producto actualizar(
            @PathVariable Long id,
            @Valid @RequestPart("producto") ProductoDTO productoDto,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen) throws IOException {

        String imageUrl = productoDto.imageUrl();

        if (imagen != null && !imagen.isEmpty()) {
            imageUrl = cloudinaryService.subirImagen(imagen);
        }

        ProductoDTO productoConImagen = new ProductoDTO(
                productoDto.nombre(),
                productoDto.descripcion(),
                productoDto.precio(),
                productoDto.stock(),
                imageUrl
        );

        return productoService.actualizar(id, productoConImagen);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
