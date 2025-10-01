package com.recetas.backend.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.domain.repository.ComentarioRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.ComentarioService;
import com.recetas.backend.service.NotificacionService;

/**
 * Implementación de los servicios relacionados con la gestión de comentarios.
 */
@Service
public class ComentarioServiceImpl implements ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;
    private final NotificacionService notificacionService;

    public ComentarioServiceImpl(ComentarioRepository comentarioRepository, UsuarioRepository usuarioRepository,
            RecetaRepository recetaRepository, NotificacionService notificacionService) {
        this.comentarioRepository = comentarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.recetaRepository = recetaRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Crea un nuevo comentario para una receta.
     * 
     * @param comentario El contenido del comentario.
     * @param usuarioId  El ID del usuario que crea el comentario.
     * @param recetaId   El ID de la receta a la que se añade el comentario.
     * @return El comentario creado.
     */
    @Override
    @Transactional
    public Comentario crearComentario(String comentario, Integer usuarioId, Integer recetaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setComentario(comentario);
        nuevoComentario.setUsuario(usuario);
        nuevoComentario.setReceta(receta);

        Comentario comentarioGuardado = comentarioRepository.save(nuevoComentario);

        // Crear notificacion
        notificacionService.crearNotificacion(receta.getUsuario().getId(), TipoNotificacion.NUEVO_COMENTARIO, usuarioId,
                recetaId.longValue());

        return comentarioGuardado;
    }

    /**
     * Elimina un comentario.
     * 
     * @param comentarioId El ID del comentario a eliminar.
     * @param usuarioId    El ID del usuario que intenta eliminar el comentario
     *                     (para verificación de permisos).
     */
    @Override
    @Transactional
    public void eliminarComentario(Integer comentarioId, Integer usuarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado"));

        // Verificar si el usuario que intenta eliminar es el autor del comentario
        // o si tiene permisos de administrador (esto último requeriría lógica
        // adicional,
        // por ahora solo permitimos al autor eliminar su propio comentario).
        if (!comentario.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permisos para eliminar este comentario.");
        }

        comentarioRepository.deleteById(comentarioId);
    }

    /**
     * Busca un comentario por su ID.
     * 
     * @param id El ID del comentario a buscar.
     * @return El comentario si se encuentra, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Comentario findById(Integer id) {
        return comentarioRepository.findById(id).orElse(null);
    }
}
