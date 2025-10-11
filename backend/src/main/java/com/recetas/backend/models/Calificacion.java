package com.recetas.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calificaciones", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "receta_id", "usuario_id" })
})
public class Calificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private Integer puntuacion; // 1-5

    @CreationTimestamp
    @Column(name = "fecha_calificacion", nullable = false, updatable = false)
    private LocalDateTime fechaCalificacion;
}
