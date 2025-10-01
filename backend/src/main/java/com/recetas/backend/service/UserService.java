package com.recetas.backend.service;

import java.util.Optional;
import java.util.Set;

import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Usuario;

/**
 * Interfaz para los servicios relacionados con la gestión de usuarios.
 */
public interface UserService {

    /**
     * Registra un nuevo usuario.
     * 
     * @param usuario El usuario a registrar.
     * @return El usuario registrado.
     */
    Usuario saveUser(Usuario usuario);

    /**
     * Busca un usuario por su correo electrónico.
     * 
     * @param email El correo electrónico del usuario.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Sigue a otro usuario.
     * 
     * @param seguidorId El ID del usuario que sigue.
     * @param seguidoId  El ID del usuario que es seguido.
     */
    void seguirUsuario(Integer seguidorId, Integer seguidoId);

    /**
     * Deja de seguir a otro usuario.
     * 
     * @param seguidorId El ID del usuario que deja de seguir.
     * @param seguidoId  El ID del usuario que deja de ser seguido.
     */
    void dejarDeSeguirUsuario(Integer seguidorId, Integer seguidoId);

    /**
     * Obtiene la lista de usuarios que siguen a un usuario dado.
     * 
     * @param userId El ID del usuario.
     * @return Un conjunto de usuarios que siguen al usuario dado.
     */
    Set<Usuario> obtenerSeguidores(Integer userId);

    /**
     * Obtiene la lista de usuarios a los que sigue un usuario dado.
     * 
     * @param userId El ID del usuario.
     * @return Un conjunto de usuarios a los que sigue el usuario dado.
     */
    Set<Usuario> obtenerSeguidos(Integer userId);

    /**
     * Busca un usuario por su ID.
     * 
     * @param id El ID del usuario a buscar.
     * @return El objeto Usuario si se encuentra, o null si no existe.
     */
    Usuario findById(Integer id);

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Usuario registrarUsuario(SignupRequestDto signupRequestDto);
}
