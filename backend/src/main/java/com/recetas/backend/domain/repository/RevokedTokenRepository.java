package com.recetas.backend.domain.repository;

import com.recetas.backend.domain.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad RevokedToken.
 * Permite gestionar los tokens JWT que han sido revocados (puestos en lista
 * negra).
 */
@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {

    /**
     * Busca un token revocado por su valor.
     * 
     * @param token El valor del token JWT.
     * @return Un Optional que contiene el RevokedToken si se encuentra, o vacío si
     *         no.
     */
    Optional<RevokedToken> findByToken(String token);

    /**
     * Verifica si un token específico ha sido revocado.
     * 
     * @param token El valor del token JWT.
     * @return true si el token ha sido revocado, false en caso contrario.
     */
    boolean existsByToken(String token);
}
