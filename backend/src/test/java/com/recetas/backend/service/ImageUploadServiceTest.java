package com.recetas.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.exception.ImageUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ImageUploadServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ImageService imageService;

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
    @DisplayName("Subir imagen exitosamente y guardar en DB")
    void uploadImage_successAndSaveToDb() throws IOException, ImageUploadException {
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
        when(imageService.saveImage(imageUrl, deleteHash))
                .thenReturn(new com.recetas.backend.domain.entity.Image(imageUrl, deleteHash));

        String resultUrl = imageUploadService.uploadImage(mockFile, "Category", "RecipeTitle");

        assertEquals(imageUrl, resultUrl);
        verify(imageService, times(1)).saveImage(imageUrl, deleteHash);
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

        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageUploadService.uploadImage(mockFile, "Category", "RecipeTitle");
        });

        assertTrue(exception.getMessage().contains("La respuesta de imgbb no contiene la URL de la imagen"));
        verify(imageService, never()).saveImage(anyString(), anyString());
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

        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageUploadService.uploadImage(mockFile, "Category", "RecipeTitle");
        });

        assertTrue(exception.getMessage().contains("Error al subir la imagen a imgbb. Código de estado: 400"));
        verify(imageService, never()).saveImage(anyString(), anyString());
    }

    @Test
    @DisplayName("Eliminar imagen exitosamente de imgbb y DB")
    void deleteImage_success() throws IOException, ImageUploadException {
        String deleteHash = "some_delete_hash";
        String imgbbResponse = "{\"data\":{\"status\":200,\"id\":\"abc\",\"title\":\"test\",\"url\":\"http://imgbb.com/uploaded_image.jpg\",\"display_url\":\"http://imgbb.com/uploaded_image.jpg\",\"size\":\"1234\",\"time\":\"123456789\",\"expiration\":\"0\",\"deletehash\":\""
                + deleteHash + "\"},\"success\":true,\"status\":200}";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>(imgbbResponse, HttpStatus.OK));
        doNothing().when(imageService).deleteImageByDeleteHash(deleteHash);

        imageUploadService.deleteImage(deleteHash);

        verify(imageService, times(1)).deleteImageByDeleteHash(deleteHash);
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

        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageUploadService.deleteImage(deleteHash);
        });

        assertTrue(exception.getMessage().contains("Error al eliminar la imagen de imgbb. Código de estado: 400"));
        verify(imageService, never()).deleteImageByDeleteHash(anyString());
    }

    @Test
    @DisplayName("Listar todas las URLs de imágenes exitosamente")
    void listAllImageUrls_success() {
        com.recetas.backend.domain.entity.Image img1 = new com.recetas.backend.domain.entity.Image("url1", "hash1");
        com.recetas.backend.domain.entity.Image img2 = new com.recetas.backend.domain.entity.Image("url2", "hash2");
        List<com.recetas.backend.domain.entity.Image> images = Arrays.asList(img1, img2);

        when(imageService.getAllImages()).thenReturn(images);

        List<String> resultUrls = imageUploadService.listAllImageUrls();

        assertNotNull(resultUrls);
        assertEquals(2, resultUrls.size());
        assertTrue(resultUrls.contains("url1"));
        assertTrue(resultUrls.contains("url2"));
        verify(imageService, times(1)).getAllImages();
    }
}
