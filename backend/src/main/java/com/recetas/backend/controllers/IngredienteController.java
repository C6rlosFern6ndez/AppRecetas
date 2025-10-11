package com.recetas.backend.controllers;

import com.recetas.backend.dtos.IngredienteDto;
import com.recetas.backend.services.IngredienteService;
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
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    @Autowired
    private IngredienteService ingredienteService;

    // Obtener todos los ingredientes paginados
    @GetMapping
    public ResponseEntity<Page<IngredienteDto>> getAllIngredientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ingredienteService.getAllIngredientes(pageable));
    }

    // Obtener ingrediente por ID
    @GetMapping("/{id}")
    public ResponseEntity<IngredienteDto> getIngredienteById(@PathVariable Integer id) {
        return ResponseEntity.ok(ingredienteService.getIngredienteById(id));
    }

    // Crear nuevo ingrediente (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<IngredienteDto> createIngrediente(@Valid @RequestBody IngredienteDto ingredienteDto) {
        return new ResponseEntity<>(ingredienteService.createIngrediente(ingredienteDto), HttpStatus.CREATED);
    }

    // Actualizar ingrediente (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<IngredienteDto> updateIngrediente(@PathVariable Integer id,
            @Valid @RequestBody IngredienteDto ingredienteDto) {
        return ResponseEntity.ok(ingredienteService.updateIngrediente(id, ingredienteDto));
    }

    // Eliminar ingrediente (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteIngrediente(@PathVariable Integer id) {
        ingredienteService.deleteIngrediente(id);
        return ResponseEntity.noContent().build();
    }
}
