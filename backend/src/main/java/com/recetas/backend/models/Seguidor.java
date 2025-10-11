package com.recetas.backend.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seguidores")
@IdClass(SeguidorId.class)
public class Seguidor {

    @Id
    @ManyToOne
    @JoinColumn(name = "seguidor_id", referencedColumnName = "id")
    private Usuario seguidor;

    @Id
    @ManyToOne
    @JoinColumn(name = "seguido_id", referencedColumnName = "id")
    private Usuario seguido;
}
