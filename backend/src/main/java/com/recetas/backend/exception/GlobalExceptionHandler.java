package com.recetas.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la aplicación.
 * Centraliza el manejo de errores para proporcionar respuestas consistentes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Maneja las excepciones de CategoriaNoEncontradaException.
         *
         * @param ex      La excepción CategoriaNoEncontradaException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(CategoriaNoEncontradaException.class)
        public ResponseEntity<ErrorDetails> handleCategoriaNoEncontradaException(CategoriaNoEncontradaException ex,
                        WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        /**
         * Maneja las excepciones de EmailAlreadyInUseException.
         *
         * @param ex      La excepción EmailAlreadyInUseException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(EmailAlreadyInUseException.class)
        public ResponseEntity<ErrorDetails> handleEmailAlreadyInUseException(EmailAlreadyInUseException ex,
                        WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
        }

        /**
         * Maneja las excepciones de validaciรณn de argumentos de mรฉtodo (ej. @Valid en
         * DTOs).
         *
         * @param ex      La excepciรณn MethodArgumentNotValidException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error de validaciรณn.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex,
                        WebRequest request) {
                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                "Error de validaciรณn",
                                request.getDescription(false),
                                errors);
                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja las excepciones de UsuarioNoEncontradoException.
         *
         * @param ex      La excepciรณn UsuarioNoEncontradoException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(UsuarioNoEncontradoException.class)
        public ResponseEntity<ErrorDetails> handleUsuarioNoEncontradoException(UsuarioNoEncontradoException ex,
                        WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        /**
         * Maneja las excepciones de RecetaNoEncontradaException.
         *
         * @param ex      La excepciรณn RecetaNoEncontradaException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(RecetaNoEncontradaException.class)
        public ResponseEntity<ErrorDetails> handleRecetaNoEncontradaException(RecetaNoEncontradaException ex,
                        WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
        }

        /**
         * Maneja las excepciones de ComentarioException.
         *
         * @param ex      La excepciรณn ComentarioException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(ComentarioException.class)
        public ResponseEntity<ErrorDetails> handleComentarioException(ComentarioException ex, WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja las excepciones de MeGustaException.
         *
         * @param ex      La excepciรณn MeGustaException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(MeGustaException.class)
        public ResponseEntity<ErrorDetails> handleMeGustaException(MeGustaException ex, WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja las excepciones de SeguimientoException.
         *
         * @param ex      La excepciรณn SeguimientoException.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error.
         */
        @ExceptionHandler(SeguimientoException.class)
        public ResponseEntity<ErrorDetails> handleSeguimientoException(SeguimientoException ex, WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

        /**
         * Maneja todas las demรกs excepciones no capturadas.
         *
         * @param ex      La excepciรณn general.
         * @param request La solicitud web actual.
         * @return ResponseEntity con los detalles del error interno del servidor.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDetails> handleGlobalException(Exception ex, WebRequest request) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                "Error interno del servidor: " + ex.getMessage(),
                                request.getDescription(false));
                return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
