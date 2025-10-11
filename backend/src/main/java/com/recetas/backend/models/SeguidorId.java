package com.recetas.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeguidorId implements Serializable {
    private Usuario seguidor;
    private Usuario seguido;
}
