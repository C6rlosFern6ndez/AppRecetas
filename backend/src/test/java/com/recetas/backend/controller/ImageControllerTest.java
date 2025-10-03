package com.recetas.backend.controller;

import com.recetas.backend.domain.entity.Image;
import com.recetas.backend.exception.ImageUploadException;
import com.recetas.backend.service.ImageService;
import com.recetas.backend.service.ImageUploadService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
@ActiveProfiles("test")
class ImageControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ImageUploadService imageUploadService;

        @MockBean
        private ImageService imageService;

        @Test
        @DisplayName("Subir imagen exitosamente")
        void uploadImage_success() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "test image content".getBytes());
                String imageUrl = "http://imgbb.com/uploaded_image.jpg";

                when(imageUploadService.uploadImage(any(MockMultipartFile.class), anyString(), anyString()))
                                .thenReturn(imageUrl);

                mockMvc.perform(multipart("/api/images/upload")
                                .file(file)
                                .param("categoryName", "TestCategory")
                                .param("recipeTitle", "TestRecipe"))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Imagen subida con éxito. URL: " + imageUrl));
        }

        @Test
        @DisplayName("Fallar al subir imagen - IOException")
        void uploadImage_ioException() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "test image content".getBytes());

                when(imageUploadService.uploadImage(any(MockMultipartFile.class), anyString(), anyString()))
                                .thenThrow(new IOException("Error de prueba de E/S"));

                mockMvc.perform(multipart("/api/images/upload")
                                .file(file)
                                .param("categoryName", "TestCategory")
                                .param("recipeTitle", "TestRecipe"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(content().string("Error de E/S al subir la imagen: Error de prueba de E/S"));
        }

        @Test
        @DisplayName("Fallar al subir imagen - ImageUploadException")
        void uploadImage_imageUploadException() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "image",
                                "test.jpg",
                                MediaType.IMAGE_JPEG_VALUE,
                                "test image content".getBytes());

                when(imageUploadService.uploadImage(any(MockMultipartFile.class), anyString(), anyString()))
                                .thenThrow(new ImageUploadException("Error de subida de imagen de prueba"));

                mockMvc.perform(multipart("/api/images/upload")
                                .file(file)
                                .param("categoryName", "TestCategory")
                                .param("recipeTitle", "TestRecipe"))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().string(
                                                "Error al subir la imagen: Error de subida de imagen de prueba"));
        }

        @Test
        @DisplayName("Eliminar imagen exitosamente")
        void deleteImage_success() throws Exception {
                String deleteHash = "testDeleteHash";
                doNothing().when(imageUploadService).deleteImage(deleteHash);

                mockMvc.perform(delete("/api/images/delete/{deleteHash}", deleteHash))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Imagen eliminada con éxito."));
        }

        @Test
        @DisplayName("Fallar al eliminar imagen - ImageUploadException")
        void deleteImage_imageUploadException() throws Exception {
                String deleteHash = "nonExistentHash";
                doThrow(new ImageUploadException("Imagen no encontrada para eliminar"))
                                .when(imageUploadService).deleteImage(deleteHash);

                mockMvc.perform(delete("/api/images/delete/{deleteHash}", deleteHash))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string(
                                                "Error al eliminar la imagen: Imagen no encontrada para eliminar"));
        }

        @Test
        @DisplayName("Obtener imagen por ID exitosamente")
        void getImageById_success() throws Exception {
                Image image = new Image("http://imgbb.com/image1.jpg", "hash1");
                image.setId(1);
                when(imageService.getImageById(1)).thenReturn(Optional.of(image));

                mockMvc.perform(get("/api/images/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.url").value("http://imgbb.com/image1.jpg"));
        }

        @Test
        @DisplayName("No encontrar imagen por ID")
        void getImageById_notFound() throws Exception {
                when(imageService.getImageById(99)).thenReturn(Optional.empty());

                mockMvc.perform(get("/api/images/{id}", 99))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Imagen no encontrada con ID: 99"));
        }

        @Test
        @DisplayName("Listar todas las URLs de imágenes exitosamente")
        void listAllImageUrls_success() throws Exception {
                List<String> urls = Arrays.asList("url1", "url2");
                when(imageUploadService.listAllImageUrls()).thenReturn(urls);

                mockMvc.perform(get("/api/images/urls"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0]").value("url1"))
                                .andExpect(jsonPath("$[1]").value("url2"));
        }

        @Test
        @DisplayName("Listar todas las imágenes (entidades) exitosamente")
        void listAllImages_success() throws Exception {
                Image img1 = new Image("url1", "hash1");
                img1.setId(1);
                Image img2 = new Image("url2", "hash2");
                img2.setId(2);
                List<Image> images = Arrays.asList(img1, img2);

                when(imageService.getAllImages()).thenReturn(images);

                mockMvc.perform(get("/api/images"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].url").value("url1"))
                                .andExpect(jsonPath("$[1].id").value(2))
                                .andExpect(jsonPath("$[1].url").value("url2"));
        }
}
