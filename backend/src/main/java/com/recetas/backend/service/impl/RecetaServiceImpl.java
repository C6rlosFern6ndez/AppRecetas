package com.recetas.backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.MeGustaReceta;
import com.recetas.backend.domain.entity.MeGustaRecetaId;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.MeGustaRecetaRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.RecetaService;

/**
 * Implementación de los servicios relacionados con la gestión de recetas.
 */
@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MeGustaRecetaRepository meGustaRecetaRepository;

    public RecetaServiceImpl(RecetaRepository recetaRepository, UsuarioRepository usuarioRepository,
            MeGustaRecetaRepository meGustaRecetaRepository) {
        this.recetaRepository = recetaRepository;
        this.usuarioRepository = usuarioRepository;
        this.meGustaRecetaRepository = meGustaRecetaRepository;
    }

    /**
     * Da "me gusta" a una receta.
     *
     * @param usuarioId El ID del usuario que da "me gusta".
     * @param recetaId  El ID de la receta a la que se da "me gusta".
     */
    @Override
    @Transactional
    public void darMeGusta(Integer usuarioId, Integer recetaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        MeGustaRecetaId id = new MeGustaRecetaId(usuarioId, recetaId);

        // Verificar si ya existe el "me gusta" para evitar duplicados
        if (meGustaRecetaRepository.existsById(id)) {
            throw new IllegalArgumentException("Ya has dado 'me gusta' a esta receta.");
        }

        MeGustaReceta meGusta = new MeGustaReceta(id, usuario, receta);
        meGustaRecetaRepository.save(meGusta);
    }

    /**
     * Quita el "me gusta" de una receta.
     *
     * @param usuarioId El ID del usuario que quita el "me gusta".
     * @param recetaId  El ID de la receta a la que se quita el "me gusta".
     */
    @Override
    @Transactional
    public void quitarMeGusta(Integer usuarioId, Integer recetaId) {

        MeGustaRecetaId id = new MeGustaRecetaId(usuarioId, recetaId);

        // Verificar si el "me gusta" existe antes de intentar eliminarlo
        if (!meGustaRecetaRepository.existsById(id)) {
            throw new IllegalArgumentException("No has dado 'me gusta' a esta receta.");
        }

        meGustaRecetaRepository.deleteById(id);
    }

    /**
     * Busca una receta por su ID.
     *
     * @param id El ID de la receta a buscar.
     * @return La receta si se encuentra, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Receta findById(Integer id) {
        return recetaRepository.findById(id).orElse(null);
    }

    @Override
    public Receta guardarReceta(Receta receta) {
        return recetaRepository.save(receta);
    }

    @Override
    public List<Receta> obtenerTodasLasRecetas() {
        return recetaRepository.findAll();
    }

    @Override
    public Optional<Receta> obtenerRecetaPorId(Integer id) {
        return recetaRepository.findById(id);
    }

    @Override
    public void eliminarReceta(Integer id) {
        recetaRepository.deleteById(id);
    }
}
