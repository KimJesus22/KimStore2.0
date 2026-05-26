package com.kimstore.pc_backend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioOrderByFechaDesc(Usuario usuario);
}
