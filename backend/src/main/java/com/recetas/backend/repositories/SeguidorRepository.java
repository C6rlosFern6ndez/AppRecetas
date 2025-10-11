package com.recetas.backend.repositories;

import com.recetas.backend.models.Seguidor;
import com.recetas.backend.models.SeguidorId;
import com.recetas.backend.models.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeguidorRepository extends JpaRepository<Seguidor, SeguidorId> {
    Page<Seguidor> findBySeguido(Usuario seguido, Pageable pageable);

    Page<Seguidor> findBySeguidor(Usuario seguidor, Pageable pageable);
}
