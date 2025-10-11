package com.recetas.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recetas")
public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tiempo_preparacion", nullable = false)
    private Integer tiempoPreparacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad;

    @Column(nullable = false)
    private Integer porciones;

    @Column(name = "url_imagen", length = 255)
    private String urlImagen;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(name = "receta_categorias", joinColumns = @JoinColumn(name = "receta_id"), inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Categoria> categorias = new HashSet<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Paso> pasos = new HashSet<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecetaIngrediente> ingredientes = new HashSet<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Calificacion> calificaciones = new HashSet<>();

    @ManyToMany(mappedBy = "recetasGustadas")
    private Set<Usuario> usuariosQueGustan = new HashSet<>();

    public enum Dificultad {
        Fácil, Media, Difícil
    }
}
