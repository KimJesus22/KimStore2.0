package com.kimstore.pc_backend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Método clave para que Spring Security busque al usuario en la base de datos
    Optional<Usuario> findByUsername(String username);
}
