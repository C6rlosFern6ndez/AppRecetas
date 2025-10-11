package com.recetas.backend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment; // Importar Environment
import org.springframework.beans.factory.annotation.Autowired; // Importar Autowired
import org.springframework.boot.CommandLineRunner; // Importar CommandLineRunner

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API de Recetas de Cocina", version = "1.0", description = "API RESTful para gestionar recetas de cocina, usuarios, categorías, ingredientes y más.", contact = @Contact(name = "Carlos Fernández", email = "carlos.fernandez@example.com"), license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
public class BackendApplication implements CommandLineRunner { // Implementar CommandLineRunner

	@Autowired
	private Environment env; // Inyectar Environment para acceder a las propiedades

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Obtener el puerto del servidor
		String serverPort = env.getProperty("server.port", "8080"); // Puerto por defecto 8080

		// Obtener la URL de la base de datos
		String dbUrl = env.getProperty("spring.datasource.url");

		// Obtener el nombre de usuario de la base de datos
		String dbUsername = env.getProperty("spring.datasource.username");

		// Imprimir la información en la consola
		System.out.println("----------------------------------------------------------");
		System.out.println("  Aplicación Backend de Recetas iniciada correctamente!");
		System.out.println("----------------------------------------------------------");
		System.out.println("  Puerto del servidor: http://localhost:" + serverPort);
		System.out.println("  Conexión a la base de datos:");
		System.out.println("    URL: " + dbUrl);
		System.out.println("    Usuario: " + dbUsername);
		System.out.println("----------------------------------------------------------");
	}
}
