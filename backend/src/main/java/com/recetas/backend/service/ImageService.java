package com.recetas.backend.service;

import com.recetas.backend.domain.entity.Image;
import com.recetas.backend.domain.repository.ImageRepository;
import com.recetas.backend.exception.ImageUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar las operaciones CRUD de las imágenes en la base de
 * datos.
 */
@Service
public class ImageService {

    private final ImageRepository imageRepository;

    /**
     * Constructor para inyectar el repositorio de imágenes.
     *
     * @param imageRepository El repositorio de imágenes.
     */
    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * Guarda una nueva imagen en la base de datos.
     *
     * @param imageUrl   La URL de la imagen.
     * @param deleteHash El hash de eliminación de la imagen.
     * @return La entidad {@link Image} guardada.
     * @throws ImageUploadException Si la URL o el deleteHash son nulos o vacíos.
     */
    @Transactional
    public Image saveImage(String imageUrl, String deleteHash) throws ImageUploadException {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new ImageUploadException("La URL de la imagen no puede ser nula o vacía.");
        }
        if (deleteHash == null || deleteHash.trim().isEmpty()) {
            throw new ImageUploadException("El hash de eliminación no puede ser nulo o vacío.");
        }
        // Comprobar si la imagen ya existe por URL para evitar duplicados
        if (imageRepository.findByUrl(imageUrl).isPresent()) {
            throw new ImageUploadException("Ya existe una imagen con la URL: " + imageUrl);
        }
        Image image = new Image(imageUrl, deleteHash);
        System.out.println("Guardando imagen en la base de datos: " + image.getUrl());
        return imageRepository.save(image);
    }

    /**
     * Obtiene una imagen por su ID.
     *
     * @param id El ID de la imagen.
     * @return Un {@link Optional} que contiene la imagen si se encuentra, o vacío
     *         si no.
     */
    @Transactional(readOnly = true)
    public Optional<Image> getImageById(Integer id) {
        System.out.println("Buscando imagen por ID: " + id);
        return imageRepository.findById(id);
    }

    /**
     * Obtiene una imagen por su URL.
     *
     * @param url La URL de la imagen.
     * @return Un {@link Optional} que contiene la imagen si se encuentra, o vacío
     *         si no.
     */
    @Transactional(readOnly = true)
    public Optional<Image> getImageByUrl(String url) {
        System.out.println("Buscando imagen por URL: " + url);
        return imageRepository.findByUrl(url);
    }

    /**
     * Obtiene todas las imágenes almacenadas en la base de datos.
     *
     * @return Una lista de todas las entidades {@link Image}.
     */
    @Transactional(readOnly = true)
    public List<Image> getAllImages() {
        System.out.println("Listando todas las imágenes.");
        return imageRepository.findAll();
    }

    /**
     * Elimina una imagen de la base de datos por su ID.
     *
     * @param id El ID de la imagen a eliminar.
     * @throws ImageUploadException Si la imagen no se encuentra.
     */
    @Transactional
    public void deleteImageById(Integer id) throws ImageUploadException {
        if (!imageRepository.existsById(id)) {
            throw new ImageUploadException("No se encontró la imagen con ID: " + id + " para eliminar.");
        }
        System.out.println("Eliminando imagen con ID: " + id);
        imageRepository.deleteById(id);
    }

    /**
     * Elimina una imagen de la base de datos por su hash de eliminación.
     *
     * @param deleteHash El hash de eliminación de la imagen a eliminar.
     * @throws ImageUploadException Si la imagen no se encuentra.
     */
    @Transactional
    public void deleteImageByDeleteHash(String deleteHash) throws ImageUploadException {
        Optional<Image> imageOptional = imageRepository.findByDeleteHash(deleteHash);
        if (imageOptional.isEmpty()) {
            throw new ImageUploadException(
                    "No se encontró la imagen con deleteHash: " + deleteHash + " para eliminar.");
        }
        System.out.println("Eliminando imagen con deleteHash: " + deleteHash);
        imageRepository.delete(imageOptional.get());
    }
}
