package com.recetas.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@EnableCaching // Habilitar el soporte de caché en la aplicación
@OpenAPIDefinition(info = @Info(title = "API de la Red Social Culinaria", version = "1.0", description = "Documentación de la API para la aplicación de recetas."))
public class App implements CommandLineRunner {

	@Value("${server.port:8080}")
	private String serverPort;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	public static void main(String[] args) {
		try {
			SpringApplication.run(App.class, args);
		} catch (Exception e) {
			if (e.getClass().getName().contains("SilentExitException")) {
				return;
			}
			System.err.println("\n--- ERROR: La aplicación no pudo iniciarse ---");
			System.err.println("Causa: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n----------------------------------------------------------");
		System.out.println("Aplicación iniciada correctamente!");
		System.out.println("  -> Puerto del servidor: " + serverPort);
		System.out.println("  -> Conectado a la BBDD: " + dbUrl);
		System.out.println("----------------------------------------------------------\n");
	}
}
