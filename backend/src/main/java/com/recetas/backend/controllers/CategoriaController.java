package com.recetas.backend.controllers;

import com.recetas.backend.dtos.CategoriaDto;
import com.recetas.backend.services.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    // Obtener todas las categorías paginadas
    @GetMapping
    public ResponseEntity<Page<CategoriaDto>> getAllCategorias(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoriaService.getAllCategorias(pageable));
    }

    // Obtener categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDto> getCategoriaById(@PathVariable Integer id) {
        return ResponseEntity.ok(categoriaService.getCategoriaById(id));
    }

    // Crear nueva categoría (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<CategoriaDto> createCategoria(@Valid @RequestBody CategoriaDto categoriaDto) {
        return new ResponseEntity<>(categoriaService.createCategoria(categoriaDto), HttpStatus.CREATED);
    }

    // Actualizar categoría (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<CategoriaDto> updateCategoria(@PathVariable Integer id,
            @Valid @RequestBody CategoriaDto categoriaDto) {
        return ResponseEntity.ok(categoriaService.updateCategoria(id, categoriaDto));
    }

    // Eliminar categoría (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable Integer id) {
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
