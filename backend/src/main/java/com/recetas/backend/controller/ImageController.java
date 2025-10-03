package com.recetas.backend.controller;

import com.recetas.backend.domain.entity.Image;
import com.recetas.backend.exception.ImageUploadException;
import com.recetas.backend.service.ImageUploadService;
import com.recetas.backend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para la gestión de imágenes.
 * Proporciona endpoints para subir, eliminar, listar y obtener imágenes.
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final ImageUploadService imageUploadService;
    private final ImageService imageService;

    /**
     * Constructor para inyectar los servicios de subida y gestión de imágenes.
     *
     * @param imageUploadService El servicio de subida de imágenes.
     * @param imageService       El servicio de gestión de imágenes en la base de
     *                           datos.
     */
    @Autowired
    public ImageController(ImageUploadService imageUploadService, ImageService imageService) {
        this.imageUploadService = imageUploadService;
        this.imageService = imageService;
    }

    /**
     * Sube una imagen a imgbb y guarda su referencia en la base de datos.
     *
     * @param imageFile    El archivo de imagen a subir.
     * @param categoryName El nombre de la categoría (opcional).
     * @param recipeTitle  El título de la receta (opcional).
     * @return ResponseEntity con la URL de la imagen subida o un mensaje de error.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(value = "categoryName", required = false) String categoryName,
            @RequestParam(value = "recipeTitle", required = false) String recipeTitle) {
        try {
            String imageUrl = imageUploadService.uploadImage(imageFile, categoryName, recipeTitle);
            System.out.println("Imagen subida y guardada con éxito. URL: " + imageUrl);
            return ResponseEntity.ok("Imagen subida con éxito. URL: " + imageUrl);
        } catch (IOException e) {
            System.err.println("Error de E/S al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error de E/S al subir la imagen: " + e.getMessage());
        } catch (ImageUploadException e) {
            System.err.println("Error al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al subir la imagen: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al subir la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al subir la imagen: " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de imgbb y de la base de datos usando su deleteHash.
     *
     * @param deleteHash El hash de eliminación de la imagen.
     * @return ResponseEntity con un mensaje de éxito o error.
     */
    @DeleteMapping("/delete/{deleteHash}")
    public ResponseEntity<String> deleteImage(@PathVariable String deleteHash) {
        try {
            imageUploadService.deleteImage(deleteHash);
            System.out.println("Imagen eliminada con éxito con deleteHash: " + deleteHash);
            return ResponseEntity.ok("Imagen eliminada con éxito.");
        } catch (ImageUploadException e) {
            System.err.println("Error al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error al eliminar la imagen: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al eliminar la imagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al eliminar la imagen: " + e.getMessage());
        }
    }

    /**
     * Obtiene una imagen por su ID de la base de datos.
     *
     * @param id El ID de la imagen.
     * @return ResponseEntity con la entidad Image o un mensaje de error.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getImageById(@PathVariable Integer id) {
        try {
            Optional<Image> image = imageService.getImageById(id);
            if (image.isPresent()) {
                System.out.println("Imagen encontrada por ID: " + id);
                return ResponseEntity.ok(image.get());
            } else {
                System.out.println("No se encontró la imagen con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Imagen no encontrada con ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("Error inesperado al obtener la imagen por ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado al obtener la imagen por ID: " + e.getMessage());
        }
    }

    /**
     * Lista todas las URLs de las imágenes almacenadas en la base de datos.
     *
     * @return ResponseEntity con una lista de URLs de imágenes o un mensaje de
     *         error.
     */
    @GetMapping("/urls")
    public ResponseEntity<List<String>> listAllImageUrls() {
        try {
            List<String> imageUrls = imageUploadService.listAllImageUrls();
            System.out.println("Listadas " + imageUrls.size() + " URLs de imágenes.");
            return ResponseEntity.ok(imageUrls);
        } catch (Exception e) {
            System.err.println("Error inesperado al listar las URLs de las imágenes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // No se devuelve el cuerpo en caso de error para List<String>
        }
    }

    /**
     * Lista todas las imágenes (entidades completas) almacenadas en la base de
     * datos.
     *
     * @return ResponseEntity con una lista de entidades Image o un mensaje de
     *         error.
     */
    @GetMapping
    public ResponseEntity<List<Image>> listAllImages() {
        try {
            List<Image> images = imageService.getAllImages();
            System.out.println("Listadas " + images.size() + " imágenes completas.");
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            System.err.println("Error inesperado al listar todas las imágenes: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // No se devuelve el cuerpo en caso de error para List<Image>
        }
    }
}
