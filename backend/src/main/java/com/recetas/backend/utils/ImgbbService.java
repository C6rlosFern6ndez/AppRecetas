package com.recetas.backend.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Service
public class ImgbbService {

    @Value("${imgbb.api.key}")
    private String imgbbApiKey;

    private final String IMGBB_UPLOAD_URL = "https://api.imgbb.com/1/upload";

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo de imagen no puede estar vacío.");
        }

        // Convertir la imagen a Base64
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        // Preparar los parámetros de la petición
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("key", imgbbApiKey);
        body.add("image", base64Image);

        // Configurar los headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Crear la entidad de la petición
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // Realizar la petición a ImgBB
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(IMGBB_UPLOAD_URL, requestEntity, Map.class);

        // Parsear la respuesta
        if (response != null && response.containsKey("data")) {
            Map<String, Object> data = (Map<String, Object>) response.get("data");
            if (data != null && data.containsKey("url")) {
                return (String) data.get("url");
            }
        }
        throw new IOException("No se pudo subir la imagen a ImgBB o no se obtuvo la URL.");
    }
}
