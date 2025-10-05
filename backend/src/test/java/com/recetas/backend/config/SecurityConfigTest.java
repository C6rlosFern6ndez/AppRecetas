package com.recetas.backend.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.recetas.backend.security.AuthEntryPointJwt;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.domain.repository.RevokedTokenRepository; // Importar RevokedTokenRepository

/**
 * Tests unitarios para la configuración de seguridad de Spring Security.
 * Verifica que la configuración se instancie correctamente y tenga los valores
 * esperados.
 */
@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

        @Mock
        private AuthEntryPointJwt authEntryPointJwt;

        @Mock
        private JwtUtils jwtUtils;

        @Mock
        private RevokedTokenRepository revokedTokenRepository; // Mock para RevokedTokenRepository

        private SecurityConfig securityConfig;

        @BeforeEach
        void setup() {
                // Se pasa el mock de RevokedTokenRepository al constructor de SecurityConfig
                securityConfig = new SecurityConfig(authEntryPointJwt, jwtUtils, revokedTokenRepository);
        }

        /**
         * Prueba que el password encoder sea BCrypt.
         */
        @Test
        void passwordEncoder_ShouldBeBCryptEncoder() {
                // Dado
                PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

                // Cuando & Verificar
                assertNotNull(passwordEncoder);
                assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);

                // Verificar que funciona
                String rawPassword = "testPassword123";
                String encodedPassword = passwordEncoder.encode(rawPassword);

                assertNotEquals(rawPassword, encodedPassword);
                assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
        }

        /**
         * Prueba que la configuración CORS tenga los orígenes permitidos.
         */
        @Test
        void corsConfigurationSource_ShouldAllowConfiguredOrigins() {
                // Dado
                CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

                // Cuando
                CorsConfiguration corsConfig = ((org.springframework.web.cors.UrlBasedCorsConfigurationSource) corsSource)
                                .getCorsConfiguration(new MockHttpServletRequest());

                // Verificar
                assertNotNull(corsConfig);

                List<String> allowedOrigins = corsConfig.getAllowedOrigins();
                assertNotNull(allowedOrigins);
                assertTrue(allowedOrigins.contains("http://localhost:4200"));
                assertTrue(allowedOrigins.contains("http://localhost:8080"));

                // Verificar que también se permite el patrón http://localhost:517*
                List<String> allowedOriginPatterns = corsConfig.getAllowedOriginPatterns();
                assertNotNull(allowedOriginPatterns);
                assertTrue(allowedOriginPatterns.contains("http://localhost:517*"));
        }

        /**
         * Prueba que la configuración CORS tenga los métodos HTTP permitidos.
         */
        @Test
        void corsConfigurationSource_ShouldAllowConfiguredMethods() {
                // Dado
                CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

                // Cuando
                CorsConfiguration corsConfig = ((org.springframework.web.cors.UrlBasedCorsConfigurationSource) corsSource)
                                .getCorsConfiguration(new MockHttpServletRequest());

                // Verificar
                assertNotNull(corsConfig);
                List<String> allowedMethods = corsConfig.getAllowedMethods();
                assertNotNull(allowedMethods);
                assertTrue(allowedMethods.contains("GET"));
                assertTrue(allowedMethods.contains("POST"));
                assertTrue(allowedMethods.contains("PUT"));
                assertTrue(allowedMethods.contains("DELETE"));
                assertTrue(allowedMethods.contains("OPTIONS"));
        }

        /**
         * Prueba que la configuración CORS incluya las headers permitidas.
         */
        @Test
        void corsConfigurationSource_ShouldAllowConfiguredHeaders() {
                // Dado
                CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

                // Cuando
                CorsConfiguration corsConfig = ((org.springframework.web.cors.UrlBasedCorsConfigurationSource) corsSource)
                                .getCorsConfiguration(new MockHttpServletRequest());

                // Verificar
                assertNotNull(corsConfig);
                List<String> allowedHeaders = corsConfig.getAllowedHeaders();
                assertNotNull(allowedHeaders);
                assertTrue(allowedHeaders.contains("Authorization"));
                assertTrue(allowedHeaders.contains("Cache-Control"));
                assertTrue(allowedHeaders.contains("Content-Type"));
        }

        /**
         * Prueba que la configuración CORS permita credenciales.
         */
        @Test
        void corsConfigurationSource_ShouldAllowCredentials() {
                // Dado
                CorsConfigurationSource corsSource = securityConfig.corsConfigurationSource();

                // Cuando
                CorsConfiguration corsConfig = ((org.springframework.web.cors.UrlBasedCorsConfigurationSource) corsSource)
                                .getCorsConfiguration(new MockHttpServletRequest());

                // Verificar
                assertNotNull(corsConfig);
                assertTrue(corsConfig.getAllowCredentials());
        }

        /**
         * Verifica que el SecurityConfig se establezca correctamente con las
         * dependencias.
         */
        @Test
        void securityConfig_ShouldProperlyInjectDependencies() {
                assertNotNull(securityConfig);
                // Los mocks se inyectaron correctamente en el constructor (si fallara, el test
                // de @BeforeEach lo haría)
        }
}
