package com.kimstore.pc_backend;

import com.kimstore.pc_backend.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerMiPerfil(Principal principal) {
        Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(Map.of(
                "username", usuario.getUsername(),
                "rol", usuario.getRol(),
                "avatarUrl", usuario.getAvatarUrl() != null ? usuario.getAvatarUrl() : ""
        ));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> actualizarAvatar(
            @RequestParam("imagen") MultipartFile imagen,
            Principal principal) {
        try {
            Usuario usuario = usuarioRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String nuevaUrl = cloudinaryService.subirImagen(imagen);

            usuario.setAvatarUrl(nuevaUrl);
            usuarioRepository.save(usuario);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Foto de perfil actualizada",
                    "avatarUrl", nuevaUrl
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Error al subir la imagen: " + e.getMessage()));
        }
    }
}
