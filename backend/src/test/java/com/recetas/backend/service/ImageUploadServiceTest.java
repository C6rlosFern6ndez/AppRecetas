package com.recetas.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ImageUploadServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private ImageUploadService imageUploadService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        // Inyectar la clave API y la URL de imgbb usando ReflectionTestUtils
        ReflectionTestUtils.setField(imageUploadService, "imgbbApiKey", "testApiKey");
        ReflectionTestUtils.setField(imageUploadService, "imgbbUploadUrl", "https://api.imgbb.com/1/upload");
        ReflectionTestUtils.setField(imageUploadService, "restTemplate", restTemplate);
        ReflectionTestUtils.setField(imageUploadService, "objectMapper", objectMapper);
    }

    /**
     * Test para verificar la subida exitosa de una imagen.
     * 
     * @throws IOException Si ocurre un error de E/S.
     */
    @Test
    void uploadImage_Success() throws IOException {
        // Configurar el comportamiento del MultipartFile mock
        when(multipartFile.getOriginalFilename()).thenReturn("test.jpg");
        when(multipartFile.getBytes()).thenReturn("test image content".getBytes());

        // Simular una respuesta exitosa de imgbb
        String successResponse = "{\"data\":{\"url\":\"http://example.com/uploaded_image.jpg\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);

        // Configurar el comportamiento del RestTemplate mock
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(responseEntity);

        // Llamar al método a testear
        String imageUrl = imageUploadService.uploadImage(multipartFile, "Category", "RecipeTitle");

        // Verificar el resultado
        assertNotNull(imageUrl, "La URL de la imagen no debería ser nula");
        assertEquals("http://example.com/uploaded_image.jpg", imageUrl, "La URL de la imagen no coincide");
    }

    /**
     * Test para verificar el manejo de una respuesta fallida de imgbb.
     * 
     * @throws IOException Si ocurre un error de E/S.
     */
    @Test
    void uploadImage_FailureResponse() throws IOException {
        // Configurar el comportamiento del MultipartFile mock
        when(multipartFile.getOriginalFilename()).thenReturn("test.png");
        when(multipartFile.getBytes()).thenReturn("test image content".getBytes());

        // Simular una respuesta fallida de imgbb (ej. status 400)
        ResponseEntity<String> responseEntity = new ResponseEntity<>("{\"error\":\"upload failed\"}",
                HttpStatus.BAD_REQUEST);

        // Configurar el comportamiento del RestTemplate mock
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(responseEntity);

        // Llamar al método a testear
        String imageUrl = imageUploadService.uploadImage(multipartFile, "Category", "RecipeTitle");

        // Verificar el resultado
        assertNull(imageUrl, "La URL de la imagen debería ser nula en caso de fallo");
    }

    /**
     * Test para verificar el manejo de una excepción durante la subida.
     * 
     * @throws IOException Si ocurre un error de E/S.
     */
    @Test
    void uploadImage_Exception() throws IOException {
        // Configurar el comportamiento del MultipartFile mock
        when(multipartFile.getOriginalFilename()).thenReturn("test.gif");
        when(multipartFile.getBytes()).thenReturn("test image content".getBytes());

        // Simular una excepción durante la llamada a RestTemplate
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenThrow(new RuntimeException("Network error"));

        // Llamar al método a testear
        String imageUrl = imageUploadService.uploadImage(multipartFile, "Category", "RecipeTitle");

        // Verificar el resultado
        assertNull(imageUrl, "La URL de la imagen debería ser nula en caso de excepción");
    }

    /**
     * Test para verificar el manejo de un nombre de archivo nulo.
     * 
     * @throws IOException Si ocurre un error de E/S.
     */
    @Test
    void uploadImage_NullOriginalFilename() throws IOException {
        // Configurar el comportamiento del MultipartFile mock con nombre de archivo
        // nulo
        when(multipartFile.getOriginalFilename()).thenReturn(null);
        when(multipartFile.getBytes()).thenReturn("test image content".getBytes());

        // Simular una respuesta exitosa de imgbb
        String successResponse = "{\"data\":{\"url\":\"http://example.com/uploaded_image.jpg\"}}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(successResponse, HttpStatus.OK);

        // Configurar el comportamiento del RestTemplate mock
        when(restTemplate.exchange(
                eq("https://api.imgbb.com/1/upload"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(responseEntity);

        // Llamar al método a testear
        String imageUrl = imageUploadService.uploadImage(multipartFile, "Category", "RecipeTitle");

        // Verificar el resultado
        assertNotNull(imageUrl, "La URL de la imagen no debería ser nula");
        assertEquals("http://example.com/uploaded_image.jpg", imageUrl, "La URL de la imagen no coincide");
    }
}
