package com.kimstore.pc_backend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // ¡Literalmente esto es todo!
    // Al heredar de JpaRepository, Spring Boot nos regala mágicamente
    // métodos como .save(), .findAll(), .findById(), .delete() sin escribir código.

    // Le agregamos "Pageable" al final y cambiamos "List" por "Page".
    Page<Producto> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND " +
            "p.precio >= :precioMin AND " +
            "p.precio <= :precioMax AND " +
            "p.stock >= :stockMin")
    List<Producto> buscarConFiltros(
            @Param("nombre") String nombre,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            @Param("stockMin") Integer stockMin
    );
}
