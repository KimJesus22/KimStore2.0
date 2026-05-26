package com.kimstore.pc_backend;

import com.kimstore.pc_backend.dto.ItemCompraDTO;
import com.kimstore.pc_backend.dto.ItemPedidoDTO;
import com.kimstore.pc_backend.dto.PedidoResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/comprar")
    @Transactional
    public ResponseEntity<?> procesarCompra(@RequestBody List<ItemCompraDTO> carrito, Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setUsuario(usuario);

        double totalPedido = 0;
        List<ItemPedido> itemsPedido = new ArrayList<>();

        for (ItemCompraDTO item : carrito) {
            Producto producto = productoRepository.findById(item.id())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            if (producto.getStock() < item.cantidad()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "No hay suficiente stock para: " + producto.getNombre()));
            }

            producto.setStock(producto.getStock() - item.cantidad());
            productoRepository.save(producto);

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setProductoId(producto.getId());
            itemPedido.setNombreProducto(producto.getNombre());
            itemPedido.setCantidad(item.cantidad());
            itemPedido.setPrecioUnitario(producto.getPrecio());
            itemPedido.setPedido(pedido);

            itemsPedido.add(itemPedido);
            totalPedido += producto.getPrecio() * item.cantidad();
        }

        pedido.setTotal(totalPedido);
        pedido.setItems(itemsPedido);
        pedidoRepository.save(pedido);

        return ResponseEntity.ok(Map.of("mensaje", "Compra registrada exitosamente"));
    }

    @GetMapping("/mis-compras")
    public ResponseEntity<List<PedidoResponseDTO>> obtenerMisCompras(Principal principal) {
        String username = principal.getName();
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Pedido> pedidos = pedidoRepository.findByUsuarioOrderByFechaDesc(usuario);
        List<PedidoResponseDTO> respuesta = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Pedido pedido : pedidos) {
            List<ItemPedidoDTO> itemsDTO = pedido.getItems().stream()
                    .map(item -> new ItemPedidoDTO(
                            item.getNombreProducto(),
                            item.getCantidad(),
                            item.getPrecioUnitario()
                    ))
                    .toList();

            respuesta.add(new PedidoResponseDTO(
                    pedido.getId(),
                    pedido.getFecha().format(formatter),
                    pedido.getTotal(),
                    itemsDTO
            ));
        }

        return ResponseEntity.ok(respuesta);
    }
}
