package com.recetas.backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.service.ImageUploadService;
import com.recetas.backend.service.RecetaService;

/**
 * Controlador REST para la gestión de recetas.
 */
@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    @Autowired
    private ImageUploadService imageUploadService;

    /**
     * Crea una nueva receta.
     *
     * @param recetaRequestDto DTO con los datos de la receta y el archivo de
     *                         imagen.
     * @return ResponseEntity con la receta creada o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<Receta> crearReceta(@ModelAttribute RecetaRequestDto recetaRequestDto) {
        try {
            // 1. Subir la imagen
            String imageUrl = null;
            if (recetaRequestDto.getImagenFile() != null && !recetaRequestDto.getImagenFile().isEmpty()) {
                imageUrl = imageUploadService.uploadImage(recetaRequestDto.getImagenFile());
                if (imageUrl == null) {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Error al subir imagen
                }
            }

            // 2. Crear la entidad Receta
            Receta receta = new Receta();
            receta.setTitulo(recetaRequestDto.getTitulo());
            receta.setDescripcion(recetaRequestDto.getDescripcion());
            receta.setTiempoPreparacion(recetaRequestDto.getTiempoPreparacion());
            receta.setDificultad(recetaRequestDto.getDificultad());
            receta.setPorciones(recetaRequestDto.getPorciones());
            receta.setUrlImagen(imageUrl);
            receta.setCategorias(recetaRequestDto.getCategorias()); // Asignar categorías

            // En un escenario real, necesitaríamos obtener el usuario autenticado
            // para asociar la receta a él. Por ahora, se guarda sin usuario asociado
            // explícitamente.

            Receta recetaGuardada = recetaService.guardarReceta(receta);
            return new ResponseEntity<>(recetaGuardada, HttpStatus.CREATED);
        } catch (IOException e) {
            // Error al procesar el archivo de imagen
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Manejo básico de otros errores
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene todas las recetas.
     *
     * @return ResponseEntity con la lista de recetas o un mensaje de error.
     */
    @GetMapping
    public ResponseEntity<List<Receta>> obtenerTodasLasRecetas() {
        try {
            List<Receta> recetas = recetaService.obtenerTodasLasRecetas();
            return new ResponseEntity<>(recetas, HttpStatus.OK);
        } catch (Exception e) {
            // Manejo básico de errores
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene una receta por su ID.
     *
     * @param id El ID de la receta.
     * @return ResponseEntity con la receta encontrada o un código 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerRecetaPorId(@PathVariable("id") Integer id) {
        Optional<Receta> recetaOptional = recetaService.obtenerRecetaPorId(id);
        return recetaOptional.map(receta -> new ResponseEntity<>(receta, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Actualiza una receta existente.
     *
     * @param id               El ID de la receta a actualizar.
     * @param recetaRequestDto Los nuevos datos de la receta y la imagen.
     * @return ResponseEntity con la receta actualizada o un código 404 si no
     *         existe.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Receta> actualizarReceta(@PathVariable("id") Integer id,
            @ModelAttribute RecetaRequestDto recetaRequestDto) {
        Optional<Receta> recetaOptional = recetaService.obtenerRecetaPorId(id);
        if (recetaOptional.isPresent()) {
            Receta recetaExistente = recetaOptional.get();

            // Subir nueva imagen si se proporciona
            String nuevaUrlImagen = recetaExistente.getUrlImagen(); // Mantener la URL actual por defecto
            try {
                if (recetaRequestDto.getImagenFile() != null && !recetaRequestDto.getImagenFile().isEmpty()) {
                    nuevaUrlImagen = imageUploadService.uploadImage(recetaRequestDto.getImagenFile());
                    if (nuevaUrlImagen == null) {
                        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Error al subir imagen
                    }
                }
            } catch (IOException e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Error al procesar archivo
            }

            // Actualizar campos de la receta
            recetaExistente.setTitulo(recetaRequestDto.getTitulo());
            recetaExistente.setDescripcion(recetaRequestDto.getDescripcion());
            recetaExistente.setTiempoPreparacion(recetaRequestDto.getTiempoPreparacion());
            recetaExistente.setDificultad(recetaRequestDto.getDificultad());
            recetaExistente.setPorciones(recetaRequestDto.getPorciones());
            recetaExistente.setUrlImagen(nuevaUrlImagen); // Usar la nueva URL de imagen si se subió
            recetaExistente.setCategorias(recetaRequestDto.getCategorias()); // Actualizar categorías

            // Las relaciones como usuario, pasos, ingredientes, comentarios,
            // calificaciones, meGustas
            // deberían manejarse con cuidado, posiblemente requiriendo DTOs más complejos o
            // lógica adicional.

            try {
                Receta recetaActualizada = recetaService.guardarReceta(recetaExistente);
                return new ResponseEntity<>(recetaActualizada, HttpStatus.OK);
            } catch (Exception e) {
                // Manejo básico de errores
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Elimina una receta por su ID.
     *
     * @param id El ID de la receta a eliminar.
     * @return ResponseEntity con código 204 (No Content) si la eliminación fue
     *         exitosa,
     *         o un código 404 si la receta no fue encontrada.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarReceta(@PathVariable("id") Integer id) {
        try {
            recetaService.eliminarReceta(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Si deleteById lanza una excepción (ej. si el ID no existe),
            // Spring Data JPA puede lanzar DataIntegrityViolationException si hay FKs.
            // Aquí un manejo genérico.
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
