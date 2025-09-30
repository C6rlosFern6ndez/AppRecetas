package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Seguidor;
import com.recetas.backend.domain.entity.SeguidorId;

@Repository
public interface SeguidorRepository extends JpaRepository<Seguidor, SeguidorId> {

    boolean existsById_SeguidorIdAndId_SeguidoId(Long seguidorId, Long seguidoId);

}
