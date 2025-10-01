package com.recetas.backend.service;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource; // Importar ByteArrayResource

/**
 * Servicio para la carga de imágenes utilizando la API de imgbb.
 */
@Service
public class ImageUploadService {

    @Value("${imgbb.api.key:9dcb1f2df4f6375a49e21a785345d86f}") // Clave API de imgbb
    private String imgbbApiKey;

    private final String imgbbUploadUrl = "https://api.imgbb.com/1/upload";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Sube una imagen a imgbb con un nombre de archivo personalizado.
     *
     * @param imageFile    El archivo de imagen a subir.
     * @param categoryName El nombre de la categoría a la que pertenece la receta.
     * @param recipeTitle  El título de la receta.
     * @return La URL de la imagen subida, o null si la subida falla.
     * @throws IOException Si ocurre un error al procesar el archivo.
     */
    public String uploadImage(MultipartFile imageFile, String categoryName, String recipeTitle) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Extraer la extensión del archivo original
        String originalFilename = imageFile.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Construir el nuevo nombre del archivo
        String newFilename = categoryName.replaceAll("\\s+", "") + "-" + recipeTitle.replaceAll("\\s+", "")
                + fileExtension;

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", imgbbApiKey);
        body.add("image", new ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return newFilename;
            }
        });
        // Puedes añadir otros parámetros como 'expiration' si lo necesitas.
        // body.add("expiration", 600); // Ejemplo: expira en 600 segundos (10 minutos)

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    imgbbUploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");
                if (dataNode.has("url")) {
                    System.out.println("Imagen subida correctamente. URL: " + dataNode.path("url").asText());
                    return dataNode.path("url").asText();
                } else {
                    // Manejar el caso donde la respuesta no contiene la URL esperada
                    System.err.println("Error: La respuesta de imgbb no contiene la URL de la imagen.");
                    return null;
                }
            } else {
                // Manejar el caso donde la respuesta no es exitosa
                System.err.println("Error al subir la imagen a imgbb. Código de estado: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Excepción al subir la imagen a imgbb: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
