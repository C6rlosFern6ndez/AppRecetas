package com.recetas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ComentarioException extends RuntimeException {

    public ComentarioException(String message) {
        super(message);
    }
}
