package com.kimstore.pc_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;

    // 2. Las reglas del Cadenero
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Activamos CORS para permitir Astro local y produccion
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Desactivamos CSRF porque no usaremos formularios tradicionales de Spring, sino Astro + JWT
                .csrf(csrf -> csrf.disable())

                // Le decimos a Spring que NO cree sesiones (cookies). Todo será "Stateless" (sin estado) mediante JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Le decimos a Spring Security que use nuestro proveedor de autenticación
                .authenticationProvider(authenticationProvider)

                // Revisamos los JWT antes de cualquier autenticacion tradicional
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        // El dashboard de metricas es exclusivo para administradores
                        .requestMatchers(HttpMethod.GET, "/api/productos/dashboard/metrics").hasRole("ADMIN")

                        // Cualquiera puede ver los productos, iniciar sesion y registrarse
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/register").permitAll()

                        // hasRole busca automaticamente el prefijo "ROLE_"
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")

                        // Rutas de pedidos: se requiere estar logueado con cualquier rol
                        .requestMatchers("/api/pedidos/comprar").authenticated()
                        .requestMatchers("/api/pedidos/mis-compras").authenticated()

                        // Cualquier otra peticion requiere estar autenticado
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:4321",
                "https://kim-store2-0.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
