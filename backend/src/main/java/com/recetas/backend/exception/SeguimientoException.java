package com.recetas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SeguimientoException extends RuntimeException {

    public SeguimientoException(String message) {
        super(message);
    }
}
