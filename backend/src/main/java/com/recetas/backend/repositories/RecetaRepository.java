package com.recetas.backend.repositories;

import com.recetas.backend.models.Receta;
import com.recetas.backend.models.Receta.Dificultad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {

    // Buscar recetas por título (parcial)
    Page<Receta> findByTituloContainingIgnoreCase(String titulo, Pageable pageable);

    // Buscar recetas por nombre de ingrediente
    @Query("SELECT r FROM Receta r JOIN r.ingredientes ri JOIN ri.ingrediente i WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :ingredienteNombre, '%'))")
    Page<Receta> findByIngredienteNombreContainingIgnoreCase(@Param("ingredienteNombre") String ingredienteNombre,
            Pageable pageable);

    // Buscar recetas por dificultad
    Page<Receta> findByDificultad(Dificultad dificultad, Pageable pageable);

    // Buscar recetas por tiempo de preparación máximo
    Page<Receta> findByTiempoPreparacionLessThanEqual(Integer tiempoPreparacionMax, Pageable pageable);

    // Buscar recetas por nombre de categoría
    @Query("SELECT r FROM Receta r JOIN r.categorias c WHERE LOWER(c.nombre) LIKE LOWER(CONCAT('%', :categoriaNombre, '%'))")
    Page<Receta> findByCategoriaNombreContainingIgnoreCase(@Param("categoriaNombre") String categoriaNombre,
            Pageable pageable);

    // Obtener recetas de un usuario específico
    Page<Receta> findByUsuarioId(Integer usuarioId, Pageable pageable);

    // Obtener recetas que le gustan a un usuario específico
    @Query("SELECT r FROM Receta r JOIN r.usuariosQueGustan u WHERE u.id = :usuarioId")
    Page<Receta> findRecetasGustadasByUsuarioId(@Param("usuarioId") Integer usuarioId, Pageable pageable);

    // Obtener recetas por categoría (para /api/categorias/{id}/recetas)
    @Query("SELECT r FROM Receta r JOIN r.categorias c WHERE c.id = :categoriaId")
    Page<Receta> findByCategoriasId(@Param("categoriaId") Integer categoriaId, Pageable pageable);
}
