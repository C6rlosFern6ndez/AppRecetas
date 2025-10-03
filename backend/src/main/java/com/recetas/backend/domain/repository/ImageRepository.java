package com.recetas.backend.domain.repository;

import com.recetas.backend.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link Image}.
 * Proporciona métodos para realizar operaciones CRUD en la tabla `imagenes`.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    /**
     * Busca una imagen por su URL.
     *
     * @param url La URL de la imagen.
     * @return Un {@link Optional} que contiene la imagen si se encuentra, o vacío
     *         si no.
     */
    Optional<Image> findByUrl(String url);

    /**
     * Busca una imagen por su hash de eliminación.
     *
     * @param deleteHash El hash de eliminación de la imagen.
     * @return Un {@link Optional} que contiene la imagen si se encuentra, o vacío
     *         si no.
     */
    Optional<Image> findByDeleteHash(String deleteHash);
}
