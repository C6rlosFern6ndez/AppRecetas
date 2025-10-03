-- V2__Add_Image_Table.sql
-- Script de migración para añadir la tabla de imágenes

CREATE TABLE `imagenes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `url` VARCHAR(255) NOT NULL UNIQUE,
  `delete_hash` VARCHAR(255) NOT NULL,
  `fecha_subida` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
