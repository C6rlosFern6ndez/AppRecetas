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
@Table(name = "notificaciones")
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emisor_id")
    private Usuario emisor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id")
    private Receta receta;

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida = false;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public enum TipoNotificacion {
        NUEVO_SEGUIDOR, ME_GUSTA_RECETA, NUEVO_COMENTARIO
    }
}
