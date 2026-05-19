package com.kimstore.pc_backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // ¡Literalmente esto es todo!
    // Al heredar de JpaRepository, Spring Boot nos regala mágicamente
    // métodos como .save(), .findAll(), .findById(), .delete() sin escribir código.
}