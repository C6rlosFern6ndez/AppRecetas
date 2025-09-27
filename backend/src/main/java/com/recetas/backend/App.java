package com.recetas.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App implements CommandLineRunner {

	@Value("${server.port:8080}")
	private String serverPort;

	@Value("${spring.datasource.url}")
	private String dbUrl;

	public static void main(String[] args) {
		try {
			SpringApplication.run(App.class, args);
		} catch (Exception e) {
			System.err.println("\n--- ERROR: La aplicación no pudo iniciarse ---");
			System.err.println("Causa: " + e.getMessage());
			System.err.println("\n--- Pasos para solucionar el problema ---");
			System.err
					.println("1. Asegúrate de que el servidor de la base de datos (MariaDB/MySQL) esté en ejecución.");
			System.err.println(
					"2. Verifica que los datos de conexión en 'application.properties' (URL, usuario, contraseña) son correctos.");
			System.err.println("3. Revisa que el puerto de la aplicación no esté ya en uso por otro proceso.");
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
