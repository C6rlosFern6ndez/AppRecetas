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
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombreUsuario;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String contrasena;

    @Column(name = "url_foto_perfil", length = 255)
    private String urlFotoPerfil;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id", nullable = false)
    private Rol rol;

    // Relaciones con seguidores (como seguidor)
    @OneToMany(mappedBy = "seguidor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seguidor> siguiendo = new HashSet<>();

    // Relaciones con seguidores (como seguido)
    @OneToMany(mappedBy = "seguido", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Seguidor> seguidores = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Receta> recetas = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comentario> comentarios = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Calificacion> calificaciones = new HashSet<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notificacion> notificaciones = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "recetas_likes", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "receta_id"))
    private Set<Receta> recetasGustadas = new HashSet<>();
}
