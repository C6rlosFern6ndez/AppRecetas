package com.recetas.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("Subir imagen exitosamente")
    void uploadImage_success() throws Exception {
        String imageUrl = "http://imgbb.com/uploaded_image.jpg";
        String deleteHash = "some_delete_hash";
        String imgbbResponse = "{\"data\":{\"id\":\"abc\",\"title\":\"test\",\"url\":\"" + imageUrl
                + "\",\"display_url\":\"" + imageUrl
                + "\",\"width\":\"100\",\"height\":\"100\",\"size\":\"1234\",\"time\":\"123456789\",\"expiration\":\"0\",\"image\":{\"extension\":\"jpg\",\"filename\":\"test.jpg\",\"mime\":\"image/jpeg\",\"name\":\"test\",\"url\":\""
                + imageUrl
                + "\"},\"thumb\":{\"extension\":\"jpg\",\"filename\":\"test_thumb.jpg\",\"mime\":\"image/jpeg\",\"name\":\"test_thumb\",\"url\":\""
                + imageUrl
                + "\"},\"medium\":{\"extension\":\"jpg\",\"filename\":\"test_medium.jpg\",\"mime\":\"image/jpeg\",\"name\":\"test_medium\",\"url\":\""
                + imageUrl + "\"},\"deletehash\":\"" + deleteHash + "\"},\"success\":true,\"status\":200}";

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test image content".getBytes());
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.OK));

        Map<String, String> result = imageUploadService.uploadImage(mockFile, "Vegetariano", "EspinacasconGarbanzos");

        assertNotNull(result);
        assertEquals(imageUrl, result.get("url"));
        assertEquals(deleteHash, result.get("deleteHash"));
        verify(restTemplate, times(1)).exchange(eq("https://api.imgbb.com/1/upload"), eq(HttpMethod.POST),
                any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Fallar al subir imagen - respuesta imgbb sin URL")
    void uploadImage_imgbbResponseMissingUrl_throwsException() throws IOException {
        String imgbbResponse = "{\"data\":{\"id\":\"abc\",\"title\":\"test\"},\"success\":true,\"status\":200}";

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test image content".getBytes());
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.OK));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.uploadImage(mockFile, "Vegetariano", "EspinacasconGarbanzos");
        });

        assertTrue(exception.getMessage()
                .contains("Error: La respuesta de imgbb no contiene la URL o el delete_hash de la imagen"));
        assertTrue(exception.getMessage()
                .contains("Respuesta completa:"));
    }

    @Test
    @DisplayName("Fallar al subir imagen - respuesta imgbb sin deleteHash")
    void uploadImage_imgbbResponseMissingDeleteHash_throwsException() throws IOException {
        String imageUrl = "http://imgbb.com/uploaded_image.jpg";
        String imgbbResponse = "{\"data\":{\"id\":\"abc\",\"title\":\"test\",\"url\":\"" + imageUrl
                + "\"},\"success\":true,\"status\":200}";

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test image content".getBytes());
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.OK));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.uploadImage(mockFile, "Vegetariano", "EspinacasconGarbanzos");
        });

        assertTrue(exception.getMessage()
                .contains("Error: La respuesta de imgbb no contiene la URL o el delete_hash de la imagen"));
    }

    @Test
    @DisplayName("Fallar al subir imagen - respuesta imgbb no exitosa")
    void uploadImage_imgbbResponseNotSuccessful_throwsException() throws IOException {
        String imgbbResponse = "{\"success\":false,\"status\":400,\"error\":{\"message\":\"Invalid API key\"}}";

        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getBytes()).thenReturn("test image content".getBytes());
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.BAD_REQUEST));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.uploadImage(mockFile, "Vegetariano", "EspinacasconGarbanzos");
        });

        assertTrue(exception.getMessage().contains("Error al subir la imagen a imgbb. Código de estado: 400"));
        assertTrue(exception.getMessage().contains("Cuerpo de la respuesta:"));
    }

    @Test
    @DisplayName("Eliminar imagen exitosamente de imgbb")
    void deleteImage_success() throws Exception {
        String deleteHash = "some_delete_hash";
        String imgbbResponse = "{\"data\":{\"status\":200,\"id\":\"abc\",\"title\":\"test\",\"url\":\"http://imgbb.com/uploaded_image.jpg\",\"display_url\":\"http://imgbb.com/uploaded_image.jpg\",\"size\":\"1234\",\"time\":\"123456789\",\"expiration\":\"0\",\"deletehash\":\""
                + deleteHash + "\"},\"success\":true,\"status\":200}";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.OK));

        imageUploadService.deleteImage(deleteHash);

        verify(restTemplate, times(1)).exchange(contains("/delete/" + deleteHash), eq(HttpMethod.DELETE),
                any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Fallar al eliminar imagen de imgbb - respuesta no exitosa")
    void deleteImage_imgbbResponseNotSuccessful_throwsException() throws IOException {
        String deleteHash = "some_delete_hash";
        String imgbbResponse = "{\"success\":false,\"status\":400,\"error\":{\"message\":\"Invalid delete hash\"}}";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.BAD_REQUEST));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.deleteImage(deleteHash);
        });

        assertTrue(exception.getMessage().contains("Error al eliminar la imagen de imgbb. Código de estado: 400"));
        assertTrue(exception.getMessage().contains("Cuerpo de la respuesta:"));
    }

    @Test
    @DisplayName("Fallar al eliminar imagen - deleteHash nulo")
    void deleteImage_nullDeleteHash_throwsException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.deleteImage(null);
        });

        assertTrue(exception.getMessage().contains("El deleteHash no puede ser nulo o vacío"));
    }

    @Test
    @DisplayName("Fallar al eliminar imagen - deleteHash vacío")
    void deleteImage_emptyDeleteHash_throwsException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.deleteImage("");
        });

        assertTrue(exception.getMessage().contains("El deleteHash no puede ser nulo o vacío"));
    }

    @Test
    @DisplayName("Fallar al subir imagen - archivo nulo")
    void uploadImage_nullFile_throwsException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.uploadImage(null, "Category", "RecipeTitle");
        });

        assertTrue(exception.getMessage().contains("El archivo de imagen no puede ser nulo o vacío"));
    }

    @Test
    @DisplayName("Fallar al subir imagen - archivo vacío")
    void uploadImage_emptyFile_throwsException() throws IOException {
        when(mockFile.isEmpty()).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            imageUploadService.uploadImage(mockFile, "Vegetariano", "EspinacasconGarbanzos");
        });

        assertTrue(exception.getMessage().contains("El archivo de imagen no puede ser nulo o vacío"));
    }
}
