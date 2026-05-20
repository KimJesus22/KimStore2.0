package com.kimstore.pc_backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    // Lee la clave secreta y el tiempo de expiración desde tu application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // 1. Extraer el nombre de usuario del Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 2. Extraer cualquier dato del Token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 3. Generar un Token nuevo para un usuario que acaba de iniciar sesión
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername()) // A quién le pertenece el token
                .setIssuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Fecha de caducidad
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Firma digital inquebrantable
                .compact(); // Construye el String final
    }

    // 4. Validar si el Token sigue siendo válido (no ha caducado y pertenece al usuario)
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Método auxiliar para descifrar el Token usando tu clave secreta
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método auxiliar para convertir tu String secreto en una Llave criptográfica
    private Key getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
