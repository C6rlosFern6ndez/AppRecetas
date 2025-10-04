package com.recetas.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ImageUploadServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private ImageUploadService imageUploadService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Inyectar manualmente el RestTemplate y ObjectMapper ya que son final
        ReflectionTestUtils.setField(imageUploadService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(imageUploadService, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(imageUploadService, "imgbbApiKey", "test_api_key");
    }

    /**
     * Test para uploadImage exitoso con parámetros válidos.
     */
    @Test
    void uploadImage_Success_WithValidParameters() throws Exception {
        // Configurar mocks
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.png");
        when(mockFile.getBytes()).thenReturn("fake_image_data".getBytes());

        // Respuesta simulada de imgbb
        String mockResponseJson = """
                {
                    "data": {
                        "url": "https://i.ibb.co/example/test.png",
                        "deletehash": "deletehash123"
                    },
                    "success": true
                }
                """;

        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(mockResponseJson, HttpStatus.OK));

        // Ejecutar el método
        Map<String, String> result = imageUploadService.uploadImage(mockFile, "Categoria", "Título Receta");

        // Verificar resultados
        assertNotNull(result);
        assertEquals("https://i.ibb.co/example/test.png", result.get("url"));
        assertEquals("deletehash123", result.get("deleteHash"));
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    public void deleteImage(String deleteHash) throws Exception {
        if (deleteHash == null || deleteHash.trim().isEmpty()) {
            throw new IllegalArgumentException("El deleteHash no puede ser nulo o vacío para eliminar una imagen.");
        }

        String imgbbDeleteUrl = "https://api.imgbb.com/1/delete/" + deleteHash + "?key="
                + "9dcb1f2df4f6375a49e21a785345d86f";
        HttpEntity<String> requestEntity = new HttpEntity<>(new HttpHeaders());

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    imgbbDeleteUrl,
                    HttpMethod.DELETE,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(response.getBody());
                if (!rootNode.path("success").asBoolean()) {
                    throw new RuntimeException(
                            "Error al eliminar la imagen de imgbb. Respuesta: " + response.getBody());
                }
            } else {
                throw new RuntimeException("Error al eliminar la imagen de imgbb. Código de estado: "
                        + response.getStatusCode() + ". Cuerpo de la respuesta: " + response.getBody());
            }
        } catch (IOException e) {
            System.err.println(
                    "Excepción de E/S al parsear la respuesta JSON de imgbb durante la eliminación: " + e.getMessage());
            throw new RuntimeException("Error de E/S al procesar la respuesta de imgbb durante la eliminación.", e);
        }
    }

}
