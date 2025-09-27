package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Receta;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {
}
