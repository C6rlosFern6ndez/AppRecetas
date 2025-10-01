package com.recetas.backend.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.model.enums.Dificultad;

/**
 * Repositorio para la entidad Receta.
 */
@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {
        // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
        // Por ejemplo, para buscar recetas por título, por usuario, etc.

        @Query("SELECT r FROM Receta r " +
                        "LEFT JOIN r.ingredientes ri " +
                        "LEFT JOIN ri.ingrediente i " + // Añadir un join a la entidad Ingrediente
                        "LEFT JOIN r.categorias rc " +
                        "WHERE (:titulo IS NULL OR LOWER(r.titulo) LIKE LOWER(CONCAT('%', :titulo, '%'))) " +
                        "AND (:ingredienteNombre IS NULL OR LOWER(i.nombre) LIKE LOWER(CONCAT('%', :ingredienteNombre, '%'))) "
                        + // Usar i.nombre
                        "AND (:dificultad IS NULL OR r.dificultad = :dificultad) " +
                        "AND (:tiempoPreparacionMax IS NULL OR r.tiempoPreparacion <= :tiempoPreparacionMax) " +
                        "AND (:categoriaNombre IS NULL OR LOWER(rc.nombre) LIKE LOWER(CONCAT('%', :categoriaNombre, '%'))) "
                        +
                        "GROUP BY r.id")
        Page<Receta> buscarRecetas(
                        @Param("titulo") String titulo,
                        @Param("ingredienteNombre") String ingredienteNombre,
                        @Param("dificultad") Dificultad dificultad,
                        @Param("tiempoPreparacionMax") Integer tiempoPreparacionMax,
                        @Param("categoriaNombre") String categoriaNombre,
                        Pageable pageable);
}
