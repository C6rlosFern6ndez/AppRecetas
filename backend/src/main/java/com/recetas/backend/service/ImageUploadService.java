package com.recetas.backend.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import java.util.HashMap;
import java.util.Map;

/**
 * Servicio para la carga y eliminación de imágenes utilizando la API de imgbb.
 * Este servicio se encarga exclusivamente de la interacción con la API externa,
 * devolviendo la URL y el hash de eliminación. La persistencia de estas
 * referencias en la base de datos debe ser gestionada por los servicios
 * correspondientes (ej. UserService para fotos de perfil, RecetaService para
 * imágenes de recetas).
 */
@Service
public class ImageUploadService {

    // Clave API de imgbb, con un valor por defecto si no se configura en
    // application.properties
    @Value("${imgbb.api.key:9dcb1f2df4f6375a49e21a785345d86f}")
    private String imgbbApiKey;

    // URL de la API de subida de imgbb
    private final String imgbbUploadUrl = "https://api.imgbb.com/1/upload";

    // RestTemplate para realizar las llamadas HTTP
    private final RestTemplate restTemplate = new RestTemplate();
    // ObjectMapper para parsear las respuestas JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sube una imagen a imgbb con un nombre de archivo personalizado.
     *
     * @param imageFile    El archivo de imagen a subir.
     * @param categoryName El nombre de la categoría a la que pertenece la receta
     *                     (puede ser null para fotos de perfil).
     * @param recipeTitle  El título de la receta (puede ser null para fotos de
     *                     perfil).
     * @return Un mapa que contiene la URL de la imagen subida ("url") y su hash de
     *         eliminación ("deleteHash").
     * @throws ImageUploadException Si la subida de la imagen falla o la respuesta
     *                              de imgbb no es válida.
     */
    public Map<String, String> uploadImage(MultipartFile imageFile, String categoryName, String recipeTitle)
            throws Exception {
        // Validar que el archivo de imagen no sea nulo o vacío
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede ser nulo o vacío.");
        }

        // Configurar las cabeceras para la solicitud multipart/form-data
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Extraer la extensión del archivo original
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generar un timestamp para asegurar un nombre de archivo único
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        // Construir el nuevo nombre del archivo, limpiando espacios y añadiendo
        // timestamp
        String baseName = "";
        if (categoryName != null && !categoryName.trim().isEmpty()) {
            baseName += categoryName.replaceAll("\\s+", "");
        }
        if (recipeTitle != null && !recipeTitle.trim().isEmpty()) {
            if (!baseName.isEmpty()) {
                baseName += "-";
            }
            baseName += recipeTitle.replaceAll("\\s+", "");
        }
        if (baseName.isEmpty()) {
            baseName = "unknown"; // Nombre por defecto si no hay categoría ni título
        }
        String newFilename = baseName + "-" + timestamp + fileExtension;

        // Crear el cuerpo de la solicitud con los parámetros obligatorios
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", imgbbApiKey); // Añadir la clave API
        try {
            body.add("image", new ByteArrayResource(imageFile.getBytes()) {
                @Override
                public String getFilename() {
                    return newFilename; // Usar el nombre de archivo generado
                }
            });
        } catch (IOException e) {
            System.err.println("Excepción de E/S al leer el archivo de imagen: " + e.getMessage());
            throw new RuntimeException("Error de E/S al procesar la imagen para subirla.", e);
        }

        // Añadir el nombre de la imagen, eliminando espacios en blanco del título de la
        // receta
        if (recipeTitle != null && !recipeTitle.trim().isEmpty()) {
            body.add("name", recipeTitle.replaceAll("\\s+", ""));
        }

        // Crear la entidad HTTP con el cuerpo y las cabeceras
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // Realizar la llamada a la API de imgbb
            ResponseEntity<String> response = restTemplate.exchange(
                    imgbbUploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Verificar si la respuesta fue exitosa
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parsear la respuesta JSON
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");
                // Comprobar si la URL de la imagen y el delete_hash están presentes en la
                // respuesta
                if (dataNode.has("url") && dataNode.has("deletehash")) {
                    String imageUrl = dataNode.path("url").asText();
                    String deleteHash = dataNode.path("deletehash").asText();

                    System.out.println("Imagen subida correctamente. URL: " + imageUrl);
                    Map<String, String> result = new HashMap<>();
                    result.put("url", imageUrl);
                    result.put("deleteHash", deleteHash);
                    return result;
                } else {
                    // Si la URL o el delete_hash no están presentes, lanzar una excepción
                    String errorMessage = "Error: La respuesta de imgbb no contiene la URL o el delete_hash de la imagen. Respuesta completa: "
                            + response.getBody();
                    System.err.println(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            } else {
                // Si la respuesta no es exitosa, lanzar una excepción con el código de estado
                String errorMessage = "Error al subir la imagen a imgbb. Código de estado: "
                        + response.getStatusCode() + ". Cuerpo de la respuesta: " + response.getBody();
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (IOException e) {
            // Capturar errores de E/S al parsear JSON
            System.err.println("Excepción de E/S al parsear la respuesta JSON de imgbb: " + e.getMessage());
            throw new RuntimeException("Error de E/S al procesar la respuesta de imgbb.", e);
        } catch (Exception e) {
            // Capturar cualquier otra excepción durante la subida
            System.err.println("Excepción inesperada al subir la imagen a imgbb: " + e.getMessage());
            throw new RuntimeException("Error inesperado durante la subida de la imagen.", e);
        }
    }

    /**
     * Elimina una imagen de imgbb.
     *
     * @param deleteHash El hash de eliminación de la imagen en imgbb.
     * @throws ImageUploadException Si la eliminación falla.
     */
    public void deleteImage(String deleteHash) throws Exception {
        // Validar que el deleteHash no sea nulo o vacío
        if (deleteHash == null || deleteHash.trim().isEmpty()) {
            throw new IllegalArgumentException("El deleteHash no puede ser nulo o vacío para eliminar una imagen.");
        }

        String imgbbDeleteUrl = "https://api.imgbb.com/1/delete/" + deleteHash + "?key=" + imgbbApiKey;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    imgbbDeleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                boolean success = rootNode.path("success").asBoolean();
                if (success) {
                    System.out.println("Imagen eliminada de imgbb con deleteHash: " + deleteHash);
                } else {
                    String errorMessage = "Error al eliminar la imagen de imgbb. Respuesta: " + response.getBody();
                    System.err.println(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            } else {
                String errorMessage = "Error al eliminar la imagen de imgbb. Código de estado: "
                        + response.getStatusCode() + ". Cuerpo de la respuesta: " + response.getBody();
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }
        } catch (IOException e) {
            System.err.println(
                    "Excepción de E/S al parsear la respuesta JSON de imgbb durante la eliminación: " + e.getMessage());
            throw new RuntimeException("Error de E/S al procesar la respuesta de imgbb durante la eliminación.", e);
        } catch (Exception e) {
            System.err.println("Excepción inesperada al eliminar la imagen de imgbb: " + e.getMessage());
            throw new RuntimeException("Error inesperado durante la eliminación de la imagen.", e);
        }
    }
}
