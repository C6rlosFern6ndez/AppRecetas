package com.recetas.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
public class TestWebConfig {
    // Esta clase habilita la integración de Spring Data con Spring MVC
    // para que los objetos Page se serialicen correctamente en los tests.
}
