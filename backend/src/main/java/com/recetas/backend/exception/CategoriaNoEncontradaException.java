package com.recetas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando no se encuentra una categoría.
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // 404 Not Found
public class CategoriaNoEncontradaException extends RuntimeException {

    public CategoriaNoEncontradaException(String message) {
        super(message);
    }
}
