package com.recetas.backend.exception;

/**
 * Excepción personalizada para errores durante la subida de imágenes.
 */
public class ImageUploadException extends RuntimeException {

    public ImageUploadException(String message) {
        super(message);
    }

    public ImageUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
