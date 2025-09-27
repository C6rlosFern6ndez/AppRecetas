package com.recetas.backend.domain.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MeGustaRecetaId implements Serializable {

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(name = "receta_id")
    private Integer recetaId;
}
