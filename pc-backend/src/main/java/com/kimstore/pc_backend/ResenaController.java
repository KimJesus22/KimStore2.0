package com.kimstore.pc_backend;

import com.kimstore.pc_backend.dto.ResenaRequestDTO;
import com.kimstore.pc_backend.dto.ResenaResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resenas")
public class ResenaController {

    @Autowired
    private ResenaRepository resenaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerResenas(@PathVariable Long productoId) {
        List<Resena> resenas = resenaRepository.findByProductoIdOrderByFechaDesc(productoId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        List<ResenaResponseDTO> respuesta = resenas.stream()
                .map(resena -> new ResenaResponseDTO(
                        resena.getId(),
                        resena.getUsuario().getUsername(),
                        resena.getCalificacion(),
                        resena.getComentario(),
                        resena.getFecha().format(formatter)
                ))
                .toList();

        return ResponseEntity.ok(respuesta);
    }

    @PostMapping
    public ResponseEntity<?> crearResena(@RequestBody ResenaRequestDTO request, Principal principal) {
        if (request.calificacion() < 1 || request.calificacion() > 5) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La calificacion debe ser entre 1 y 5 estrellas"));
        }

        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(request.productoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Resena nuevaResena = new Resena();
        nuevaResena.setCalificacion(request.calificacion());
        nuevaResena.setComentario(request.comentario());
        nuevaResena.setFecha(LocalDateTime.now());
        nuevaResena.setProducto(producto);
        nuevaResena.setUsuario(usuario);

        resenaRepository.save(nuevaResena);

        return ResponseEntity.ok(Map.of("mensaje", "Resena publicada exitosamente"));
    }
}
