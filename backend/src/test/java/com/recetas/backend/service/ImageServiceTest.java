package com.recetas.backend.service;

import com.recetas.backend.domain.entity.Image;
import com.recetas.backend.domain.repository.ImageRepository;
import com.recetas.backend.exception.ImageUploadException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    private Image testImage1;
    private Image testImage2;

    @BeforeEach
    void setUp() {
        testImage1 = new Image("http://example.com/image1.jpg", "deletehash1");
        testImage1.setId(1);
        testImage2 = new Image("http://example.com/image2.jpg", "deletehash2");
        testImage2.setId(2);
    }

    @Test
    @DisplayName("Guardar una nueva imagen exitosamente")
    void saveImage_success() throws ImageUploadException {
        when(imageRepository.findByUrl(testImage1.getUrl())).thenReturn(Optional.empty());
        when(imageRepository.save(any(Image.class))).thenReturn(testImage1);

        Image savedImage = imageService.saveImage(testImage1.getUrl(), testImage1.getDeleteHash());

        assertNotNull(savedImage);
        assertEquals(testImage1.getUrl(), savedImage.getUrl());
        verify(imageRepository, times(1)).findByUrl(testImage1.getUrl());
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    @DisplayName("Fallar al guardar imagen con URL nula")
    void saveImage_nullUrl_throwsException() {
        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage(null, "deletehash");
        });
        assertEquals("La URL de la imagen no puede ser nula o vacía.", exception.getMessage());
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    @DisplayName("Fallar al guardar imagen con deleteHash nulo")
    void saveImage_nullDeleteHash_throwsException() {
        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage("http://example.com/image.jpg", null);
        });
        assertEquals("El hash de eliminación no puede ser nulo o vacío.", exception.getMessage());
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    @DisplayName("Fallar al guardar imagen con URL duplicada")
    void saveImage_duplicateUrl_throwsException() {
        when(imageRepository.findByUrl(testImage1.getUrl())).thenReturn(Optional.of(testImage1));

        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageService.saveImage(testImage1.getUrl(), testImage1.getDeleteHash());
        });
        assertEquals("Ya existe una imagen con la URL: " + testImage1.getUrl(), exception.getMessage());
        verify(imageRepository, times(1)).findByUrl(testImage1.getUrl());
        verify(imageRepository, never()).save(any(Image.class));
    }

    @Test
    @DisplayName("Obtener imagen por ID exitosamente")
    void getImageById_success() {
        when(imageRepository.findById(1)).thenReturn(Optional.of(testImage1));

        Optional<Image> foundImage = imageService.getImageById(1);

        assertTrue(foundImage.isPresent());
        assertEquals(testImage1.getUrl(), foundImage.get().getUrl());
        verify(imageRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("No encontrar imagen por ID")
    void getImageById_notFound() {
        when(imageRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Image> foundImage = imageService.getImageById(99);

        assertFalse(foundImage.isPresent());
        verify(imageRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Obtener imagen por URL exitosamente")
    void getImageByUrl_success() {
        when(imageRepository.findByUrl(testImage1.getUrl())).thenReturn(Optional.of(testImage1));

        Optional<Image> foundImage = imageService.getImageByUrl(testImage1.getUrl());

        assertTrue(foundImage.isPresent());
        assertEquals(testImage1.getId(), foundImage.get().getId());
        verify(imageRepository, times(1)).findByUrl(testImage1.getUrl());
    }

    @Test
    @DisplayName("No encontrar imagen por URL")
    void getImageByUrl_notFound() {
        when(imageRepository.findByUrl("http://nonexistent.com/image.jpg")).thenReturn(Optional.empty());

        Optional<Image> foundImage = imageService.getImageByUrl("http://nonexistent.com/image.jpg");

        assertFalse(foundImage.isPresent());
        verify(imageRepository, times(1)).findByUrl("http://nonexistent.com/image.jpg");
    }

    @Test
    @DisplayName("Obtener todas las imágenes exitosamente")
    void getAllImages_success() {
        List<Image> images = Arrays.asList(testImage1, testImage2);
        when(imageRepository.findAll()).thenReturn(images);

        List<Image> allImages = imageService.getAllImages();

        assertNotNull(allImages);
        assertEquals(2, allImages.size());
        verify(imageRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Eliminar imagen por ID exitosamente")
    void deleteImageById_success() throws ImageUploadException {
        when(imageRepository.existsById(1)).thenReturn(true);
        doNothing().when(imageRepository).deleteById(1);

        imageService.deleteImageById(1);

        verify(imageRepository, times(1)).existsById(1);
        verify(imageRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Fallar al eliminar imagen por ID no encontrada")
    void deleteImageById_notFound_throwsException() {
        when(imageRepository.existsById(99)).thenReturn(false);

        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageService.deleteImageById(99);
        });
        assertEquals("No se encontró la imagen con ID: 99 para eliminar.", exception.getMessage());
        verify(imageRepository, times(1)).existsById(99);
        verify(imageRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Eliminar imagen por deleteHash exitosamente")
    void deleteImageByDeleteHash_success() throws ImageUploadException {
        when(imageRepository.findByDeleteHash(testImage1.getDeleteHash())).thenReturn(Optional.of(testImage1));
        doNothing().when(imageRepository).delete(testImage1);

        imageService.deleteImageByDeleteHash(testImage1.getDeleteHash());

        verify(imageRepository, times(1)).findByDeleteHash(testImage1.getDeleteHash());
        verify(imageRepository, times(1)).delete(testImage1);
    }

    @Test
    @DisplayName("Fallar al eliminar imagen por deleteHash no encontrado")
    void deleteImageByDeleteHash_notFound_throwsException() {
        when(imageRepository.findByDeleteHash("nonexistenthash")).thenReturn(Optional.empty());

        ImageUploadException exception = assertThrows(ImageUploadException.class, () -> {
            imageService.deleteImageByDeleteHash("nonexistenthash");
        });
        assertEquals("No se encontró la imagen con deleteHash: nonexistenthash para eliminar.", exception.getMessage());
        verify(imageRepository, times(1)).findByDeleteHash("nonexistenthash");
        verify(imageRepository, never()).delete(any(Image.class));
    }
}
