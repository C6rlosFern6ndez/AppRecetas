package com.recetas.backend.controllers;

import com.recetas.backend.dtos.CalificacionDto;
import com.recetas.backend.dtos.ComentarioDto;
import com.recetas.backend.dtos.RecetaDto;
import com.recetas.backend.models.Receta.Dificultad;
import com.recetas.backend.security.UserDetailsImpl;
import com.recetas.backend.services.RecetaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    @Autowired
    private RecetaService recetaService;

    // Obtener todas las recetas paginadas con filtros de búsqueda
    @GetMapping
    public ResponseEntity<Page<RecetaDto>> searchRecetas(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String ingredienteNombre,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Integer tiempoPreparacionMax,
            @RequestParam(required = false) String categoriaNombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecetaDto> recetas = recetaService.searchRecetas(titulo, ingredienteNombre, dificultad,
                tiempoPreparacionMax, categoriaNombre, pageable);
        return ResponseEntity.ok(recetas);
    }

    // Obtener receta por ID
    @GetMapping("/{id}")
    public ResponseEntity<RecetaDto> getRecetaById(@PathVariable Integer id) {
        return ResponseEntity.ok(recetaService.getRecetaById(id));
    }

    // Crear nueva receta
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<RecetaDto> createReceta(
            @Valid @RequestBody RecetaDto recetaDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return new ResponseEntity<>(recetaService.createReceta(recetaDto, userDetails.getId()), HttpStatus.CREATED);
    }

    // Actualizar receta
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<RecetaDto> updateReceta(
            @PathVariable Integer id,
            @Valid @RequestBody RecetaDto recetaDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(recetaService.updateReceta(id, recetaDto, userDetails.getId()));
    }

    // Eliminar receta
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceta(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        recetaService.deleteReceta(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // Subir imagen de receta
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/imagen")
    public ResponseEntity<String> uploadRecetaImagen(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) throws IOException {
        String imageUrl = recetaService.uploadRecetaImagen(id, file);
        return ResponseEntity.ok(imageUrl);
    }

    // Eliminar imagen de receta
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/imagen")
    public ResponseEntity<Void> deleteRecetaImagen(@PathVariable Integer id) {
        recetaService.deleteRecetaImagen(id);
        return ResponseEntity.noContent().build();
    }

    // Dar/quitar like a receta
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> toggleLikeReceta(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        recetaService.toggleLikeReceta(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // Obtener número de likes de una receta
    @GetMapping("/{id}/likes/count")
    public ResponseEntity<Long> getRecetaLikesCount(@PathVariable Integer id) {
        return ResponseEntity.ok(recetaService.getRecetaLikesCount(id));
    }

    // Verificar si el usuario ha dado like a una receta
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/likes/user")
    public ResponseEntity<Boolean> isRecetaLikedByUser(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(recetaService.isRecetaLikedByUser(id, userDetails.getId()));
    }

    // Calificar receta
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/calificar")
    public ResponseEntity<CalificacionDto> calificarReceta(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody CalificacionDto calificacionDto) {
        return new ResponseEntity<>(recetaService.calificarReceta(id, userDetails.getId(), calificacionDto),
                HttpStatus.CREATED);
    }

    // Obtener calificación de un usuario para una receta
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/calificacion/user")
    public ResponseEntity<Optional<CalificacionDto>> getCalificacionUsuario(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(recetaService.getCalificacionUsuario(id, userDetails.getId()));
    }

    // Obtener todas las calificaciones de una receta
    @GetMapping("/{id}/calificaciones")
    public ResponseEntity<Page<CalificacionDto>> getRecetaCalificaciones(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recetaService.getRecetaCalificaciones(id, pageable));
    }

    // Añadir comentario a receta
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<ComentarioDto> addComentario(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody ComentarioDto comentarioDto) {
        return new ResponseEntity<>(recetaService.addComentario(id, userDetails.getId(), comentarioDto),
                HttpStatus.CREATED);
    }

    // Obtener comentarios de una receta
    @GetMapping("/{id}/comentarios")
    public ResponseEntity<Page<ComentarioDto>> getRecetaComentarios(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recetaService.getRecetaComentarios(id, pageable));
    }

    // Añadir categoría a receta (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @PostMapping("/{recetaId}/categorias/{categoriaId}")
    public ResponseEntity<Void> addCategoriaToReceta(
            @PathVariable Integer recetaId,
            @PathVariable Integer categoriaId) {
        recetaService.addCategoriaToReceta(recetaId, categoriaId);
        return ResponseEntity.noContent().build();
    }

    // Eliminar categoría de receta (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/{recetaId}/categorias/{categoriaId}")
    public ResponseEntity<Void> removeCategoriaFromReceta(
            @PathVariable Integer recetaId,
            @PathVariable Integer categoriaId) {
        recetaService.removeCategoriaFromReceta(recetaId, categoriaId);
        return ResponseEntity.noContent().build();
    }

    // Obtener recetas de un usuario específico
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Page<RecetaDto>> getRecetasByUsuario(
            @PathVariable Integer usuarioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recetaService.getRecetasByUsuario(usuarioId, pageable));
    }

    // Obtener recetas favoritas de un usuario específico
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/usuario/{usuarioId}/favoritas")
    public ResponseEntity<Page<RecetaDto>> getRecetasFavoritasByUsuario(
            @PathVariable Integer usuarioId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        // Asegurarse de que el usuario autenticado solo pueda ver sus propias recetas
        // favoritas
        if (!usuarioId.equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recetaService.getRecetasFavoritasByUsuario(usuarioId, pageable));
    }

    // Obtener recetas por categoría
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<RecetaDto>> getRecetasByCategoria(
            @PathVariable Integer categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(recetaService.getRecetasByCategoria(categoriaId, pageable));
    }
}
