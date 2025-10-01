package com.recetas.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;

/**
 * Configuración para Swagger UI usando springdoc-openapi.
 * Esta clase se encarga de configurar la documentación de la API.
 */
@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

        /**
         * Configura el bean OpenAPI para la API.
         * Define la información básica de la API como título, descripción, versión,
         * etc.
         *
         * @return OpenAPI configurado.
         */
        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API de Recetas")
                                                .version("1.0.0")
                                                .description("Documentación de la API para la aplicación de recetas."))
                                .addServersItem(new Server().url("http://localhost:8080")); // Añade el servidor local
        }

        /**
         * Configura los grupos de APIs que se expondrán en Swagger UI.
         * En este caso, se expone cualquier controlador.
         *
         * @return GroupedOpenApi configurado.
         */
        @Bean
        public GroupedOpenApi publicApi() {
                return GroupedOpenApi.builder()
                                .group("public-api") // Nombre del grupo
                                .pathsToMatch("/api/**") // Rutas que incluirá este grupo
                                .packagesToScan("com.recetas.backend.controller") // Paquetes a escanear
                                .build();
        }

        /**
         * Configura los manejadores de recursos para Swagger UI.
         * Esto es importante para que Swagger UI pueda cargar sus archivos estáticos
         * correctamente.
         *
         * @param registry El registro de manejadores de recursos.
         */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/**")
                                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi-ui/");
                registry.addResourceHandler("/webjars/**")
                                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
}
