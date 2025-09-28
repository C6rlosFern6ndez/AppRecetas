package com.recetas.backend.security;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro que intercepta las peticiones para validar el token JWT.
 * Si el token es válido, autentica al usuario y lo añade al
 * SecurityContextHolder.
 */
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService; // Implementación de UserDetailsService (aún no creada)

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Realiza el filtrado de la petición.
     *
     * @param request     La petición HTTP.
     * @param response    La respuesta HTTP.
     * @param filterChain La cadena de filtros.
     * @throws IOException      Si ocurre un error de I/O.
     * @throws ServletException Si ocurre un error de Servlet.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("No se puede establecer la autenticación del usuario: {}", e.getMessage());
            // Aquí podrías manejar el error de forma más específica, por ejemplo,
            // devolviendo un 401
            // a través del AuthEntryPointJwt si el token es inválido o expirado.
            // Por ahora, simplemente registramos el error y continuamos la cadena de
            // filtros.
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Obtiene el token JWT de la cabecera 'Authorization' de la petición.
     *
     * @param request La petición HTTP.
     * @return El token JWT si existe, o null en caso contrario.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7, headerAuth.length());
        }

        return null;
    }
}
