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
@Table(name = "receta_ingredientes")
@IdClass(RecetaIngredienteId.class)
public class RecetaIngrediente {

    @Id
    @ManyToOne
    @JoinColumn(name = "receta_id", referencedColumnName = "id")
    private Receta receta;

    @Id
    @ManyToOne
    @JoinColumn(name = "ingrediente_id", referencedColumnName = "id")
    private Ingrediente ingrediente;

    @Column(length = 50)
    private String cantidad;
}
