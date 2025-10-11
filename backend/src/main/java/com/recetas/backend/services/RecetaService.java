package com.recetas.backend.services;

import com.recetas.backend.dtos.*;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.*;
import com.recetas.backend.models.Receta.Dificultad;
import com.recetas.backend.repositories.*;
import com.recetas.backend.utils.ImgbbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private PasoRepository pasoRepository;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private RecetaIngredienteRepository recetaIngredienteRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private CalificacionRepository calificacionRepository;

    @Autowired
    private NotificacionService notificacionService;

    @Autowired
    private ImgbbService imgbbService;

    public Page<RecetaDto> getAllRecetas(Pageable pageable) {
        return recetaRepository.findAll(pageable).map(RecetaDto::fromEntity);
    }

    public RecetaDto getRecetaById(Integer id) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + id));
        return RecetaDto.fromEntity(receta);
    }

    @Transactional
    public RecetaDto createReceta(RecetaDto recetaDto, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Receta receta = new Receta();
        receta.setTitulo(recetaDto.getTitulo());
        receta.setDescripcion(recetaDto.getDescripcion());
        receta.setTiempoPreparacion(recetaDto.getTiempoPreparacion());
        receta.setDificultad(recetaDto.getDificultad());
        receta.setPorciones(recetaDto.getPorciones());
        receta.setUsuario(usuario);
        receta.setFechaCreacion(LocalDateTime.now());

        // Guardar la receta para obtener el ID antes de manejar relaciones
        receta = recetaRepository.save(receta);

        // Manejar categorías
        if (recetaDto.getCategorias() != null) {
            for (CategoriaDto categoriaDto : recetaDto.getCategorias()) {
                Categoria categoria = categoriaRepository.findById(categoriaDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Categoría no encontrada con ID: " + categoriaDto.getId()));
                receta.getCategorias().add(categoria);
            }
        }

        // Manejar pasos
        if (recetaDto.getPasos() != null) {
            for (PasoDto pasoDto : recetaDto.getPasos()) {
                Paso paso = new Paso();
                paso.setReceta(receta);
                paso.setOrden(pasoDto.getOrden());
                paso.setDescripcion(pasoDto.getDescripcion());
                receta.getPasos().add(paso);
            }
        }

        // Manejar ingredientes
        if (recetaDto.getIngredientes() != null) {
            for (RecetaIngredienteDto riDto : recetaDto.getIngredientes()) {
                Ingrediente ingrediente = ingredienteRepository.findById(riDto.getIngredienteId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Ingrediente no encontrado con ID: " + riDto.getIngredienteId()));
                RecetaIngrediente recetaIngrediente = new RecetaIngrediente();
                recetaIngrediente.setReceta(receta);
                recetaIngrediente.setIngrediente(ingrediente);
                recetaIngrediente.setCantidad(riDto.getCantidad());
                receta.getIngredientes().add(recetaIngrediente);
            }
        }

        return RecetaDto.fromEntity(recetaRepository.save(receta));
    }

    @Transactional
    public RecetaDto updateReceta(Integer id, RecetaDto recetaDto, Integer usuarioId) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + id));

        if (!receta.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para actualizar esta receta.");
        }

        receta.setTitulo(recetaDto.getTitulo());
        receta.setDescripcion(recetaDto.getDescripcion());
        receta.setTiempoPreparacion(recetaDto.getTiempoPreparacion());
        receta.setDificultad(recetaDto.getDificultad());
        receta.setPorciones(recetaDto.getPorciones());

        // Actualizar categorías
        if (recetaDto.getCategorias() != null) {
            receta.getCategorias().clear(); // Limpiar existentes
            for (CategoriaDto categoriaDto : recetaDto.getCategorias()) {
                Categoria categoria = categoriaRepository.findById(categoriaDto.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Categoría no encontrada con ID: " + categoriaDto.getId()));
                receta.getCategorias().add(categoria);
            }
        }

        // Actualizar pasos (requiere lógica más compleja para
        // añadir/eliminar/modificar)
        // Por simplicidad, aquí se eliminarán y se volverán a añadir
        if (recetaDto.getPasos() != null) {
            pasoRepository.deleteAll(receta.getPasos()); // Eliminar todos los pasos existentes
            receta.getPasos().clear();
            for (PasoDto pasoDto : recetaDto.getPasos()) {
                Paso paso = new Paso();
                paso.setReceta(receta);
                paso.setOrden(pasoDto.getOrden());
                paso.setDescripcion(pasoDto.getDescripcion());
                receta.getPasos().add(paso);
            }
        }

        // Actualizar ingredientes (requiere lógica más compleja para
        // añadir/eliminar/modificar)
        // Por simplicidad, aquí se eliminarán y se volverán a añadir
        if (recetaDto.getIngredientes() != null) {
            recetaIngredienteRepository.deleteAll(receta.getIngredientes()); // Eliminar todos los ingredientes
                                                                             // existentes
            receta.getIngredientes().clear();
            for (RecetaIngredienteDto riDto : recetaDto.getIngredientes()) {
                Ingrediente ingrediente = ingredienteRepository.findById(riDto.getIngredienteId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Ingrediente no encontrado con ID: " + riDto.getIngredienteId()));
                RecetaIngrediente recetaIngrediente = new RecetaIngrediente();
                recetaIngrediente.setReceta(receta);
                recetaIngrediente.setIngrediente(ingrediente);
                recetaIngrediente.setCantidad(riDto.getCantidad());
                receta.getIngredientes().add(recetaIngrediente);
            }
        }

        return RecetaDto.fromEntity(recetaRepository.save(receta));
    }

    @Transactional
    public void deleteReceta(Integer id, Integer usuarioId) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + id));

        if (!receta.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta receta.");
        }
        recetaRepository.delete(receta);
    }

    public Page<RecetaDto> searchRecetas(String titulo, String ingredienteNombre, Dificultad dificultad,
            Integer tiempoPreparacionMax, String categoriaNombre, Pageable pageable) {
        if (titulo != null) {
            return recetaRepository.findByTituloContainingIgnoreCase(titulo, pageable).map(RecetaDto::fromEntity);
        }
        if (ingredienteNombre != null) {
            return recetaRepository.findByIngredienteNombreContainingIgnoreCase(ingredienteNombre, pageable)
                    .map(RecetaDto::fromEntity);
        }
        if (dificultad != null) {
            return recetaRepository.findByDificultad(dificultad, pageable).map(RecetaDto::fromEntity);
        }
        if (tiempoPreparacionMax != null) {
            return recetaRepository.findByTiempoPreparacionLessThanEqual(tiempoPreparacionMax, pageable)
                    .map(RecetaDto::fromEntity);
        }
        if (categoriaNombre != null) {
            return recetaRepository.findByCategoriaNombreContainingIgnoreCase(categoriaNombre, pageable)
                    .map(RecetaDto::fromEntity);
        }
        return getAllRecetas(pageable);
    }

    @Transactional
    public String uploadRecetaImagen(Integer recetaId, MultipartFile file) throws IOException {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));

        String imageUrl = imgbbService.uploadImage(file);
        receta.setUrlImagen(imageUrl);
        recetaRepository.save(receta);
        return imageUrl;
    }

    @Transactional
    public void deleteRecetaImagen(Integer recetaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));

        // Opcional: eliminar la imagen de ImgBB si la API lo permite
        receta.setUrlImagen(null);
        recetaRepository.save(receta);
    }

    @Transactional
    public void toggleLikeReceta(Integer recetaId, Integer usuarioId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        if (usuario.getRecetasGustadas().contains(receta)) {
            usuario.getRecetasGustadas().remove(receta);
            // notificacionService.deleteLikeNotification(usuarioId, recetaId); // Si se
            // implementa
        } else {
            usuario.getRecetasGustadas().add(receta);
            // notificacionService.createLikeNotification(usuarioId, recetaId); // Si se
            // implementa
        }
        usuarioRepository.save(usuario);
    }

    public Long getRecetaLikesCount(Integer recetaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        return (long) receta.getUsuariosQueGustan().size();
    }

    public Boolean isRecetaLikedByUser(Integer recetaId, Integer usuarioId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return usuario.getRecetasGustadas().contains(receta);
    }

    @Transactional
    public CalificacionDto calificarReceta(Integer recetaId, Integer usuarioId, CalificacionDto calificacionDto) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Optional<Calificacion> existingCalificacion = calificacionRepository.findByRecetaIdAndUsuarioId(recetaId,
                usuarioId);
        Calificacion calificacion;

        if (existingCalificacion.isPresent()) {
            calificacion = existingCalificacion.get();
            calificacion.setPuntuacion(calificacionDto.getPuntuacion());
            calificacion.setFechaCalificacion(LocalDateTime.now());
        } else {
            calificacion = new Calificacion();
            calificacion.setReceta(receta);
            calificacion.setUsuario(usuario);
            calificacion.setPuntuacion(calificacionDto.getPuntuacion());
            calificacion.setFechaCalificacion(LocalDateTime.now());
        }
        return CalificacionDto.fromEntity(calificacionRepository.save(calificacion));
    }

    public Optional<CalificacionDto> getCalificacionUsuario(Integer recetaId, Integer usuarioId) {
        return calificacionRepository.findByRecetaIdAndUsuarioId(recetaId, usuarioId)
                .map(CalificacionDto::fromEntity);
    }

    public Page<CalificacionDto> getRecetaCalificaciones(Integer recetaId, Pageable pageable) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        return calificacionRepository.findByReceta(receta, pageable).map(CalificacionDto::fromEntity);
    }

    @Transactional
    public ComentarioDto addComentario(Integer recetaId, Integer usuarioId, ComentarioDto comentarioDto) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Comentario comentario = new Comentario();
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        comentario.setComentario(comentarioDto.getComentario());
        comentario.setFechaComentario(LocalDateTime.now());

        return ComentarioDto.fromEntity(comentarioRepository.save(comentario));
    }

    public Page<ComentarioDto> getRecetaComentarios(Integer recetaId, Pageable pageable) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        return comentarioRepository.findByReceta(receta, pageable).map(ComentarioDto::fromEntity);
    }

    @Transactional
    public void addCategoriaToReceta(Integer recetaId, Integer categoriaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));

        receta.getCategorias().add(categoria);
        recetaRepository.save(receta);
    }

    @Transactional
    public void removeCategoriaFromReceta(Integer recetaId, Integer categoriaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + categoriaId));

        receta.getCategorias().remove(categoria);
        recetaRepository.save(receta);
    }

    public Page<RecetaDto> getRecetasByUsuario(Integer usuarioId, Pageable pageable) {
        return recetaRepository.findByUsuarioId(usuarioId, pageable).map(RecetaDto::fromEntity);
    }

    public Page<RecetaDto> getRecetasFavoritasByUsuario(Integer usuarioId, Pageable pageable) {
        return recetaRepository.findRecetasGustadasByUsuarioId(usuarioId, pageable).map(RecetaDto::fromEntity);
    }

    public Page<RecetaDto> getRecetasByCategoria(Integer categoriaId, Pageable pageable) {
        return recetaRepository.findByCategoriasId(categoriaId, pageable).map(RecetaDto::fromEntity);
    }
}
