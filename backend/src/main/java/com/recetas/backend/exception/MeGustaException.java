package com.recetas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MeGustaException extends RuntimeException {

    public MeGustaException(String message) {
        super(message);
    }
}
