package com.kimstore.pc_backend;

import com.kimstore.pc_backend.config.JwtService;
import com.kimstore.pc_backend.dto.AuthResponse;
import com.kimstore.pc_backend.dto.LoginRequest;
import com.kimstore.pc_backend.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<com.kimstore.pc_backend.dto.AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String tokenGenerado = jwtService.generateToken(userDetails);

        String userRole = userDetails.getAuthorities().stream()
                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        return ResponseEntity.ok(new com.kimstore.pc_backend.dto.AuthResponse(tokenGenerado, userRole));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByUsername(request.username());
        if (usuarioExistente.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre de usuario ya esta en uso"));
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.username());
        nuevoUsuario.setPassword(passwordEncoder.encode(request.password()));
        nuevoUsuario.setRol("ROLE_USER");

        usuarioRepository.save(nuevoUsuario);

        return ResponseEntity.ok(Map.of("mensaje", "Usuario registrado exitosamente"));
    }
}
