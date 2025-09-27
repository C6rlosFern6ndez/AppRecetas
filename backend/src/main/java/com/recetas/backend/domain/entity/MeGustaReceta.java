package com.recetas.backend.domain.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "me_gusta_recetas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MeGustaReceta {

    @EmbeddedId
    private MeGustaRecetaId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @MapsId("recetaId")
    @JoinColumn(name = "receta_id")
    private Receta receta;
}
