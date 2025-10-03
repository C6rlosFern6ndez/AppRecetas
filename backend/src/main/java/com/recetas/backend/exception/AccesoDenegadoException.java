package com.recetas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para indicar que un usuario no tiene permiso para
 * realizar una acción.
 * Se mapea a un estado HTTP 403 (Forbidden).
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccesoDenegadoException extends RuntimeException {

    /**
     * Constructor por defecto.
     */
    public AccesoDenegadoException() {
        super();
    }

    /**
     * Constructor con mensaje.
     * 
     * @param message Mensaje de la excepción.
     */
    public AccesoDenegadoException(String message) {
        super(message);
    }

    /**
     * Constructor con mensaje y causa.
     * 
     * @param message Mensaje de la excepción.
     * @param cause   Causa de la excepción.
     */
    public AccesoDenegadoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor con causa.
     * 
     * @param cause Causa de la excepción.
     */
    public AccesoDenegadoException(Throwable cause) {
        super(cause);
    }
}
