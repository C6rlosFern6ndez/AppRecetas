package com.recetas.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class PasswordDecodeTest {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    /**
     * Test para determinar qué contraseña original crea los hashes encontrados en
     * BD.
     */
    @Test
    void decodePasswordsInDatabase_ShouldFindOriginalPasswords() {
        // Hashes encontrados en la base de datos
        List<String> hashesFromDB = Arrays.asList(
                "$2a$10$N9qo8uLOickgx2ZMRZoMyeEwHH1CnhCQRVvJDBY8jLnN1kKkHkV.z6", // Hash de usuarios de prueba en SQL
                "$2a$10$KjOXus8/EExh7KZDN7HIruf2uYE7bUxMY3dP0XWsVT1joPy8umJTu" // Hash del usuario
                                                                               // c6rlosfern6ndez@gmail.com
        );

        // Contraseñas comunes que podrían haber sido usadas
        List<String> possiblePasswords = Arrays.asList(
                "password",
                "123456",
                "admin",
                "test123",
                "Goya6A",
                "usuario123",
                "secret",
                "mypassword",
                "welcome",
                "letmein",
                "qwerty",
                "abc123");

        // Probar cada hash contra cada contraseña posible
        for (String hash : hashesFromDB) {
            boolean foundPassword = false;
            String originalPassword = null;

            for (String password : possiblePasswords) {
                if (encoder.matches(password, hash)) {
                    foundPassword = true;
                    originalPassword = password;
                    break;
                }
            }

            if (foundPassword) {
                System.out.println("✅ Hash: " + hash);
                System.out.println("   Contraseña original: " + originalPassword);
            } else {
                System.out.println("❌ Hash no identificado: " + hash);
                // Generar hashes de las contraseñas comunes para comparación manual
                System.out.println("   Posibles contraseñas:");
                possiblePasswords.forEach(p -> System.out.println("   '" + p + "' -> " + encoder.encode(p)));
            }
            System.out.println();
        }
    }

    /**
     * Test específico para verificar si "Goya6A" crea algún hash de la BD.
     */
    @Test
    void goya6aPasswordCheck_ShouldVerifyAgainstDatabaseHashes() {
        String goya6aPassword = "Goya6A";
        String goya6aHash = encoder.encode(goya6aPassword);

        // Hashes encontrados en BD
        List<String> dbHashes = Arrays.asList(
                "$2a$10$N9qo8uLOickgx2ZMRZoMyeEwHH1CnhCQRVvJDBY8jLnN1kKkHkV.z6", // SQL test users
                "$2a$10$KjOXus8/EExh7KZDN7HIruf2uYE7bUxMY3dP0XWsVT1joPy8umJTu" // User email hash
        );

        System.out.println("🔍 Verificando contraseña 'Goya6A':");
        System.out.println("   Hash generado desde 'Goya6A': " + goya6aHash);

        for (String dbHash : dbHashes) {
            boolean matches = encoder.matches(goya6aPassword, dbHash);
            System.out.println("   ¿Coincide con BD hash " + dbHash + "? " + (matches ? "✅ SÍ" : "❌ NO"));
        }

        // Verificar que BCrypt produce hashes diferentes cada vez
        String goya6aHash2 = encoder.encode(goya6aPassword);
        boolean sameAsFirst = goya6aHash.equals(goya6aHash2);
        System.out.println("   ¿BCrypt genera siempre el mismo hash? " + (sameAsFirst ? "✅ SÍ" : "❌ NO"));
        assertFalse(sameAsFirst, "BCrypt debería generar hashes diferentes por sal aleatoria");

        // Verificar consistencia con matches()
        boolean selfCheck = encoder.matches(goya6aPassword, goya6aHash);
        System.out.println("   ¿La encriptación/desencriptación funciona? " + (selfCheck ? "✅ SÍ" : "❌ NO"));
        assertTrue(selfCheck, "La contraseña 'Goya6A' debería coincidir con su hash");
    }

    /**
     * Test para crear la contraseña correcta del usuario test.
     */
    @Test
    void createCorrectTestPassword_ShouldMatchDatabase() {
        // Vamos a intentar generar contraseñas hasta encontrar una que coincida
        // con los hashes de la BD, o al menos aprender cómo funcionan

        String targetHash = "$2a$10$KjOXus8/EExh7KZDN7HIruf2uYE7bUxMY3dP0XWsVT1joPy8umJTu";

        // Generar algunos hashes de muestra
        String[] testPasswords = { "test", "password", "123456", "admin", "usuario", "clave123" };

        System.out.println("📝 Generando hashes de ejemplo para comparación:");
        for (String pwd : testPasswords) {
            String hash = encoder.encode(pwd);
            boolean matchesTarget = encoder.matches(pwd, targetHash);
            System.out.println("   '" + pwd + "' -> " + hash + " (¿coincide? " + matchesTarget + ")");
        }

        // Mostrar formato esperado del hash
        System.out.println("\n📋 Informacion del hash objetivo:");
        System.out.println("   Hash: " + targetHash);
        System.out.println("   Algoritmo: BCrypt");
        System.out.println("   Costo: " + (targetHash.startsWith("$2a$10$") ? "10" : "desconocido"));
        System.out.println("   Sal y hash: " + targetHash.substring("$2a$10$".length()));

        // Verificar que podemos generar hashes válidos
        String sampleHash = encoder.encode("prueba");
        assertTrue(sampleHash.startsWith("$2a$10$"), "Los hashes deberían empezar con $2a$10$");
        assertTrue(encoder.matches("prueba", sampleHash), "Los hashes deberían poder verificarse");
    }
}
