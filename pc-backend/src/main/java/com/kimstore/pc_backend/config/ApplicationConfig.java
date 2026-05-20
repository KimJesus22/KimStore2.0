package com.kimstore.pc_backend.config;

import com.kimstore.pc_backend.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // 1. El encriptador de contrasenas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Buscador de usuarios
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            com.kimstore.pc_backend.Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            return User.builder()
                    .username(usuario.getUsername())
                    .password(usuario.getPassword())
                    .authorities(usuario.getRol())
                    .build();
        };
    }

    // 3. Proveedor de autenticacion
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 4. El administrador general
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CommandLineRunner initAdmin(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            com.kimstore.pc_backend.Usuario admin;
            java.util.Optional<com.kimstore.pc_backend.Usuario> usuarioExistente = repository.findByUsername("kim");

            if (usuarioExistente.isEmpty()) {
                admin = new com.kimstore.pc_backend.Usuario();
                admin.setUsername("kim");
                admin.setRol("ROLE_ADMIN");
                System.out.println("====== CREANDO NUEVO ADMINISTRADOR 'kim' ======");
            } else {
                admin = usuarioExistente.get();
                System.out.println("====== ACTUALIZANDO CONTRASENA DEL ADMINISTRADOR 'kim' ======");
            }

            admin.setPassword(passwordEncoder.encode("123456"));
            repository.save(admin);
            System.out.println("====== CONTRASENA SETEADA A '123456' CON EXITO ======");
        };
    }
}
