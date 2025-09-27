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
public class SeguidorId implements Serializable {

    @Column(name = "seguidor_id")
    private Integer seguidorId;

    @Column(name = "seguido_id")
    private Integer seguidoId;
}
