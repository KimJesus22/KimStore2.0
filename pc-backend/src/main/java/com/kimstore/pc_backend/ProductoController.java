package com.kimstore.pc_backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // Le dice a Spring Boot que esta clase responderá peticiones web con datos JSON
@RequestMapping("/api/productos") // Esta será la URL base para esta clase
@CrossOrigin(origins = "*") // ¡Magia pura! Esto permite que tu frontend en Astro se pueda conectar sin que el navegador lo bloquee.
public class ProductoController {

    @Autowired // Esto "inyecta" nuestro repositorio automáticamente sin tener que usar 'new ProductoRepository()'
    private ProductoRepository productoRepository;

    // 1. Método para OBTENER todos los productos (Para mostrar el catálogo en Astro)
    @GetMapping
    public List<Producto> obtenerTodosLosProductos() {
        return productoRepository.findAll(); // ¡Usa el método que nos regaló el repositorio!
    }

    // 2. Método para GUARDAR un nuevo producto en la base de datos
    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoRepository.save(producto);
    }

    // 3. Método para ELIMINAR un producto por su ID
    @DeleteMapping("/{id}")
    public void eliminarProducto(@PathVariable Long id) {
        // El mesero le dice al cocinero: "Borra el producto con este número"
        productoRepository.deleteById(id);
    }

    // 4. Método para ACTUALIZAR un producto existente
    @PutMapping("/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @RequestBody Producto productoActualizado) {
        // 1. Buscamos el producto original en la base de datos
        Producto productoExistente = productoRepository.findById(id).orElse(null);

        // 2. Si lo encontramos, le actualizamos los datos
        if (productoExistente != null) {
            productoExistente.setNombre(productoActualizado.getNombre());
            productoExistente.setDescripcion(productoActualizado.getDescripcion());
            productoExistente.setPrecio(productoActualizado.getPrecio());
            productoExistente.setStock(productoActualizado.getStock());

            // 3. Lo guardamos de vuelta en la despensa
            return productoRepository.save(productoExistente);
        }

        return null; // Si no existe, no devolvemos nada
    }

}
