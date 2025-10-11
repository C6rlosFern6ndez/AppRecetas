package com.recetas.backend.services;

import com.recetas.backend.dtos.UsuarioDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Seguidor;
import com.recetas.backend.models.SeguidorId;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.SeguidorRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private SeguidorRepository seguidorRepository;

    public UsuarioDto getUsuarioById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return UsuarioDto.fromEntity(usuario);
    }

    @Transactional
    public UsuarioDto updateUsuario(Integer id, UsuarioDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        usuario.setNombreUsuario(usuarioDto.getNombreUsuario());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setUrlFotoPerfil(usuarioDto.getUrlFotoPerfil());

        return UsuarioDto.fromEntity(usuarioRepository.save(usuario));
    }

    @Transactional
    public void deleteUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public void followUsuario(Integer seguidorId, Integer seguidoId) {
        Usuario seguidor = usuarioRepository.findById(seguidorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario seguidor no encontrado con ID: " + seguidorId));
        Usuario seguido = usuarioRepository.findById(seguidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario seguido no encontrado con ID: " + seguidoId));

        SeguidorId id = new SeguidorId(seguidor, seguido);
        if (seguidorRepository.existsById(id)) {
            throw new RuntimeException("Ya sigues a este usuario.");
        }

        Seguidor nuevaRelacion = new Seguidor(seguidor, seguido);
        seguidorRepository.save(nuevaRelacion);
    }

    @Transactional
    public void unfollowUsuario(Integer seguidorId, Integer seguidoId) {
        Usuario seguidor = usuarioRepository.findById(seguidorId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Usuario seguidor no encontrado con ID: " + seguidorId));
        Usuario seguido = usuarioRepository.findById(seguidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario seguido no encontrado con ID: " + seguidoId));

        SeguidorId id = new SeguidorId(seguidor, seguido);
        if (!seguidorRepository.existsById(id)) {
            throw new RuntimeException("No sigues a este usuario.");
        }

        seguidorRepository.deleteById(id);
    }

    public Page<UsuarioDto> getSeguidores(Integer usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return seguidorRepository.findBySeguido(usuario, pageable)
                .map(Seguidor::getSeguidor)
                .map(UsuarioDto::fromEntity);
    }

    public Page<UsuarioDto> getSiguiendo(Integer usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return seguidorRepository.findBySeguidor(usuario, pageable)
                .map(Seguidor::getSeguido)
                .map(UsuarioDto::fromEntity);
    }

    public Page<UsuarioDto> getAllUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable).map(UsuarioDto::fromEntity);
    }
}
