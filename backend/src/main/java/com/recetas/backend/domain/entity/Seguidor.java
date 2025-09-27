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
@Table(name = "seguidores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seguidor {

    @EmbeddedId
    private SeguidorId id;

    @ManyToOne
    @MapsId("seguidorId")
    @JoinColumn(name = "seguidor_id")
    private Usuario seguidor;

    @ManyToOne
    @MapsId("seguidoId")
    @JoinColumn(name = "seguido_id")
    private Usuario seguido;
}
