package com.recetas.backend.repositories;

import com.recetas.backend.models.Paso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasoRepository extends JpaRepository<Paso, Integer> {
}
