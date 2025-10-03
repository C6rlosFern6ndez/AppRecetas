package com.recetas.backend.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una imagen subida a imgbb.
 * Almacena la URL de la imagen y su hash de eliminación.
 */
@Entity
@Table(name = "imagenes")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(name = "delete_hash", nullable = false)
    private String deleteHash;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    /**
     * Constructor por defecto.
     */
    public Image() {
    }

    /**
     * Constructor para crear una nueva instancia de Image.
     *
     * @param url        La URL de la imagen.
     * @param deleteHash El hash de eliminación de la imagen.
     */
    public Image(String url, String deleteHash) {
        this.url = url;
        this.deleteHash = deleteHash;
        this.fechaSubida = LocalDateTime.now();
    }

    // Getters y Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDeleteHash() {
        return deleteHash;
    }

    public void setDeleteHash(String deleteHash) {
        this.deleteHash = deleteHash;
    }

    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }

    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    /**
     * Representación en cadena de la entidad Image.
     *
     * @return Una cadena que representa la imagen.
     */
    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", deleteHash='" + deleteHash + '\'' +
                ", fechaSubida=" + fechaSubida +
                '}';
    }
}
