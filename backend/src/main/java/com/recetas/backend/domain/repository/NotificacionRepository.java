package com.recetas.backend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Notificacion;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {

    /**
     * Encuentra todas las notificaciones para un usuario específico.
     * 
     * @param usuarioId El ID del usuario.
     * @return Una lista de notificaciones asociadas al usuario.
     */
    List<Notificacion> findByUsuarioId(Integer usuarioId);
}
