package com.recetas.backend.service.impl;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Rol;
import com.recetas.backend.domain.entity.Seguidor;
import com.recetas.backend.domain.entity.SeguidorId;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.RolRepository;
import com.recetas.backend.domain.repository.SeguidorRepository;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.NotificacionService;
import com.recetas.backend.service.UserService;

/**
 * Implementación de los servicios relacionados con la gestión de usuarios.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final SeguidorRepository seguidorRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository; // Added RolRepository for assigning roles
    private final NotificacionService notificacionService;

    public UserServiceImpl(UsuarioRepository usuarioRepository, SeguidorRepository seguidorRepository,
            PasswordEncoder passwordEncoder, RolRepository rolRepository, NotificacionService notificacionService) { // Added
                                                                                                                     // RolRepository
                                                                                                                     // to
                                                                                                                     // constructor
        this.usuarioRepository = usuarioRepository;
        this.seguidorRepository = seguidorRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolRepository = rolRepository; // Initialize RolRepository
        this.notificacionService = notificacionService;
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param signupRequestDto DTO con los datos del nuevo usuario.
     * @return El usuario registrado.
     * @throws IllegalArgumentException si el correo electrónico ya está en uso.
     */
    @Override
    @Transactional
    public Usuario registrarUsuario(SignupRequestDto signupRequestDto) {
        // Verificar si el correo electrónico ya está en uso
        if (usuarioRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya está en uso.");
        }

        // Crear un nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(signupRequestDto.getNombreUsuario());
        usuario.setEmail(signupRequestDto.getEmail());
        usuario.setContrasena(passwordEncoder.encode(signupRequestDto.getContrasena()));

        // Asignar rol por defecto (ej. USER)
        Rol rolUsuario = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol por defecto no encontrado"));
        usuario.setRol(rolUsuario);

        return usuarioRepository.save(usuario);
    }

    /**
     * Registra un nuevo usuario (método sobrecargado para compatibilidad, pero se
     * prefiere registrarUsuario(SignupRequestDto)).
     * Este método podría ser obsoleto o requerir ajustes si la lógica de registro
     * cambia.
     *
     * @param usuario El usuario a registrar.
     * @return El usuario registrado.
     */
    @Transactional
    public Usuario saveUser(Usuario usuario) {
        // Este método parece ser una forma más antigua de guardar usuarios.
        // La lógica de registro ahora se maneja en registrarUsuario(SignupRequestDto).
        // Se mantiene por compatibilidad, pero se recomienda usar registrarUsuario.
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email El correo electrónico del usuario.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    @Transactional(readOnly = true)
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param nombreUsuario El nombre de usuario a buscar.
     * @return Un Optional que contiene el usuario si se encuentra, o vacío si no.
     */
    @Override
    public Optional<Usuario> findByNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario);
    }

    /**
     * Sigue a otro usuario.
     *
     * @param seguidorId El ID del usuario que sigue.
     * @param seguidoId  El ID del usuario que es seguido.
     */
    @Override
    @Transactional
    public void seguirUsuario(Integer seguidorId, Integer seguidoId) {
        // Comprobar que un usuario no puede seguirse a si mismo
        if (seguidorId.equals(seguidoId)) {
            throw new IllegalArgumentException("Un usuario no puede seguirse a sí mismo.");
        }
        // Comprobar que los usuarios existen
        Usuario seguidor = usuarioRepository.findById(seguidorId)
                .orElseThrow(
                        () -> new RuntimeException("Usuario seguidor no encontrado con id: " + seguidorId));
        Usuario seguido = usuarioRepository.findById(seguidoId)
                .orElseThrow(
                        () -> new RuntimeException("Usuario seguido no encontrado con id: " + seguidoId));

        // Comprobar si ya le sigue
        if (seguidorRepository.existsById_SeguidorIdAndId_SeguidoId(seguidorId.longValue(), seguidoId.longValue())) {
            throw new IllegalStateException("Ya sigues a este usuario.");
        }

        SeguidorId id = new SeguidorId(seguidorId, seguidoId);
        Seguidor relacionSeguidor = new Seguidor(id, seguidor, seguido);

        seguidorRepository.save(relacionSeguidor);

        // Crear notificacion
        notificacionService.crearNotificacion(seguidoId, TipoNotificacion.NUEVO_SEGUIDOR, seguidorId, null);
    }

    /**
     * Deja de seguir a otro usuario.
     *
     * @param seguidorId El ID del usuario que deja de seguir.
     * @param seguidoId  El ID del usuario que deja de ser seguido.
     */
    @Override
    @Transactional
    public void dejarDeSeguirUsuario(Integer seguidorId, Integer seguidoId) {
        // Comprobar que los usuarios existen
        if (!usuarioRepository.existsById(seguidorId)) {
            throw new RuntimeException("Usuario seguidor no encontrado con id: " + seguidorId);
        }
        if (!usuarioRepository.existsById(seguidoId)) {
            throw new RuntimeException("Usuario seguido no encontrado con id: " + seguidoId);
        }

        SeguidorId id = new SeguidorId(seguidorId, seguidoId);

        // Comprobar si ya le sigue
        if (!seguidorRepository.existsById(id)) {
            throw new IllegalStateException("No sigues a este usuario.");
        }

        seguidorRepository.deleteById(id);
    }

    /**
     * Obtiene la lista de usuarios que siguen a un usuario dado.
     *
     * @param userId El ID del usuario.
     * @return Un conjunto de usuarios que siguen al usuario dado.
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Usuario> obtenerSeguidores(Integer userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Set<Usuario> seguidores = new HashSet<>();
        if (usuario.getSeguidores() != null) {
            for (Seguidor seguidorRelacion : usuario.getSeguidores()) {
                seguidores.add(seguidorRelacion.getSeguidor());
            }
        }
        return seguidores;
    }

    /**
     * Obtiene la lista de usuarios a los que sigue un usuario dado.
     *
     * @param userId El ID del usuario.
     * @return Un conjunto de usuarios a los que sigue el usuario dado.
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Usuario> obtenerSeguidos(Integer userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + userId));

        Set<Usuario> seguidos = new HashSet<>();
        if (usuario.getSeguidos() != null) {
            for (Seguidor seguidorRelacion : usuario.getSeguidos()) {
                seguidos.add(seguidorRelacion.getSeguido());
            }
        }
        return seguidos;
    }

    /**
     * Busca un usuario por su ID.
     *
     * @param id El ID del usuario a buscar.
     * @return El objeto Usuario si se encuentra, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Usuario findById(Integer id) {
        return usuarioRepository.findById(id).orElse(null);
    }
}
