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
     * Sube una imagen a imgbb.
     *
     * @param imageFile El archivo de imagen a subir.
     * @return La URL de la imagen subida, o null si la subida falla.
     * @throws IOException Si ocurre un error al procesar el archivo.
     */
    public String uploadImage(MultipartFile imageFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("key", imgbbApiKey);
        body.add("image", new ByteArrayResource(imageFile.getBytes(), imageFile.getOriginalFilename()));
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

    // Clase interna para envolver los bytes del archivo con el nombre del archivo
    private static class ByteArrayResource extends org.springframework.core.io.ByteArrayResource {
        private final String filename;

        public ByteArrayResource(byte[] content, String filename) {
            super(content);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }
    }
}
