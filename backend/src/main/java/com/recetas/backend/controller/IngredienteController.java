package com.recetas.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.entity.Ingrediente;
import com.recetas.backend.service.IngredienteService;

@RestController
@RequestMapping("/api/ingredientes")
public class IngredienteController {

    private final IngredienteService ingredienteService;

    public IngredienteController(IngredienteService ingredienteService) {
        this.ingredienteService = ingredienteService;
    }

    @GetMapping
    public ResponseEntity<List<Ingrediente>> getAllIngredientes() {
        return ResponseEntity.ok(ingredienteService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ingrediente> getIngredienteById(@PathVariable Integer id) {
        return ingredienteService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Ingrediente> createIngrediente(@RequestBody Ingrediente ingrediente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ingredienteService.save(ingrediente));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ingrediente> updateIngrediente(@PathVariable Integer id,
            @RequestBody Ingrediente ingrediente) {
        return ingredienteService.findById(id)
                .map(ingredienteExistente -> {
                    ingrediente.setId(id);
                    return ResponseEntity.ok(ingredienteService.save(ingrediente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngrediente(@PathVariable Integer id) {
        return ingredienteService.findById(id)
                .map(ingrediente -> {
                    ingredienteService.deleteById(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
