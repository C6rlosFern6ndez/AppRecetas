package com.recetas.backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import javax.crypto.SecretKey;

/**
 * Utilidad para generar y validar tokens JWT.
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Estas propiedades deberían venir de application.properties o application.yml
    // Por ahora, se definen valores de ejemplo.
    @Value("${app.jwt.secret:supersecretkey}") // Clave secreta para firmar los tokens
    private String jwtSecret;

    @Value("${app.jwt.expirationMs:86400000}") // Tiempo de expiración del token en milisegundos (24 horas)
    private int jwtExpirationMs;

    /**
     * Genera un token JWT a partir de la autenticación del usuario.
     *
     * @param authentication La autenticación del usuario.
     * @return El token JWT generado.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key) // Firma el token con la clave secreta
                .compact();
    }

    /**
     * Obtiene el nombre de usuario a partir de un token JWT.
     *
     * @param token El token JWT.
     * @return El nombre de usuario.
     */
    public String getUserNameFromJwtToken(String token) {
        // Usando la API correcta para versiones modernas de JJWT
        // Se utiliza verifyWith y build para obtener un JwtParser, y luego
        // parseClaimsJws
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    /**
     * Valida la firma y la estructura del token JWT.
     *
     * @param authToken El token JWT a validar.
     * @return true si el token es válido, false en caso contrario.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Usando la API correcta para versiones modernas de JJWT
            // Se utiliza verifyWith y build para obtener un JwtParser, y luego
            // parseClaimsJws
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            logger.error("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT mal formado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Tipo de token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Argumento inválido para JWT: {}", e.getMessage());
        }

        return false;
    }
}
