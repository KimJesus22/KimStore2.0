package com.kimstore.pc_backend;

import com.kimstore.pc_backend.dto.DashboardMetricsDTO;
import com.kimstore.pc_backend.dto.ItemCompraDTO;
import com.kimstore.pc_backend.dto.ProductoDTO;
import com.kimstore.pc_backend.service.CloudinaryService;
import com.kimstore.pc_backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*") // Para que Astro pueda hablar con Java sin problemas
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoRepository productoRepository;

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

    @PutMapping(path = "/{id}", consumes = {"application/json"})
    public Producto actualizarJson(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDto) {

        return productoService.actualizar(id, productoDto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @PostMapping("/comprar")
    @Transactional
    public ResponseEntity<?> procesarCompra(@RequestBody List<ItemCompraDTO> carrito) {
        for (ItemCompraDTO item : carrito) {
            Producto producto = productoRepository.findById(item.id())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.id()));

            if (producto.getStock() < item.cantidad()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No hay suficiente stock para: " + producto.getNombre()));
            }

            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);
        }

        return ResponseEntity.ok(Map.of("mensaje", "Compra procesada con exito"));
    }

    // --- NUEVO ENDPOINT PARA EL DASHBOARD DE ADMINISTRADOR ---
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        List<Producto> productos = productoRepository.findAll();

        long totalProductos = productos.size();

        double valorTotal = productos.stream()
                .mapToDouble(p -> p.getPrecio() * p.getStock())
                .sum();

        long stockBajo = productos.stream()
                .filter(p -> p.getStock() < 3)
                .count();

        return ResponseEntity.ok(new DashboardMetricsDTO(totalProductos, valorTotal, stockBajo));
    }
}
