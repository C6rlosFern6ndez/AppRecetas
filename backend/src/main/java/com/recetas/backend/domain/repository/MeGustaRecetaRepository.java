package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.MeGustaReceta;
import com.recetas.backend.domain.entity.MeGustaRecetaId;

@Repository
public interface MeGustaRecetaRepository extends JpaRepository<MeGustaReceta, MeGustaRecetaId> {

    boolean existsById_UsuarioIdAndId_RecetaId(Integer usuarioId, Integer recetaId);

}
