Drop database if exists recetas_db;

-- Usar una base de datos (cámbiala por el nombre que prefieras)
CREATE DATABASE IF NOT EXISTS `recetas_db`;
USE `recetas_db`;

-- Tabla de Usuarios
CREATE TABLE `usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre_usuario` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `contrasena` VARCHAR(255) NOT NULL,
  `fecha_registro` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `nombre_usuario_UNIQUE` (`nombre_usuario` ASC)
) ENGINE=InnoDB;

-- Tabla de Categorías
CREATE TABLE `categorias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC)
) ENGINE=InnoDB;

-- Tabla de Ingredientes
CREATE TABLE `ingredientes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC)
) ENGINE=InnoDB;

-- Tabla de Recetas
CREATE TABLE `recetas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(100) NOT NULL,
  `descripcion` TEXT NOT NULL,
  `tiempo_preparacion` INT NULL,
  `dificultad` ENUM('Fácil', 'Media', 'Difícil') NULL,
  `porciones` INT NULL,
  `usuario_id` INT NOT NULL,
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_recetas_usuarios_idx` (`usuario_id` ASC),
  CONSTRAINT `fk_recetas_usuarios`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `usuarios` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla de Pasos
CREATE TABLE `pasos` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `receta_id` INT NOT NULL,
  `orden` INT NOT NULL,
  `descripcion` TEXT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_pasos_recetas_idx` (`receta_id` ASC),
  CONSTRAINT `fk_pasos_recetas`
    FOREIGN KEY (`receta_id`)
    REFERENCES `recetas` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla Intermedia: Receta_Categorias
CREATE TABLE `receta_categorias` (
  `receta_id` INT NOT NULL,
  `categoria_id` INT NOT NULL,
  PRIMARY KEY (`receta_id`, `categoria_id`),
  INDEX `fk_receta_categorias_categorias_idx` (`categoria_id` ASC),
  INDEX `fk_receta_categorias_recetas_idx` (`receta_id` ASC),
  CONSTRAINT `fk_receta_categorias_recetas`
    FOREIGN KEY (`receta_id`)
    REFERENCES `recetas` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_receta_categorias_categorias`
    FOREIGN KEY (`categoria_id`)
    REFERENCES `categorias` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla Intermedia: Receta_Ingredientes
CREATE TABLE `receta_ingredientes` (
  `receta_id` INT NOT NULL,
  `ingrediente_id` INT NOT NULL,
  `cantidad` VARCHAR(50) NULL,
  PRIMARY KEY (`receta_id`, `ingrediente_id`),
  INDEX `fk_receta_ingredientes_ingredientes_idx` (`ingrediente_id` ASC),
  INDEX `fk_receta_ingredientes_recetas_idx` (`receta_id` ASC),
  CONSTRAINT `fk_receta_ingredientes_recetas`
    FOREIGN KEY (`receta_id`)
    REFERENCES `recetas` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_receta_ingredientes_ingredientes`
    FOREIGN KEY (`ingrediente_id`)
    REFERENCES `ingredientes` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla de Comentarios
CREATE TABLE `comentarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `receta_id` INT NOT NULL,
  `usuario_id` INT NOT NULL,
  `comentario` TEXT NOT NULL,
  `fecha_comentario` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_comentarios_recetas_idx` (`receta_id` ASC),
  INDEX `fk_comentarios_usuarios_idx` (`usuario_id` ASC),
  CONSTRAINT `fk_comentarios_recetas`
    FOREIGN KEY (`receta_id`)
    REFERENCES `recetas` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_comentarios_usuarios`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `usuarios` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabla de Calificaciones
CREATE TABLE `calificaciones` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `receta_id` INT NOT NULL,
  `usuario_id` INT NOT NULL,
  `puntuacion` INT NOT NULL,
  `fecha_calificacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `unique_calificacion` (`receta_id` ASC, `usuario_id` ASC),
  INDEX `fk_calificaciones_recetas_idx` (`receta_id` ASC),
  INDEX `fk_calificaciones_usuarios_idx` (`usuario_id` ASC),
  CONSTRAINT `fk_calificaciones_recetas`
    FOREIGN KEY (`receta_id`)
    REFERENCES `recetas` (`id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_calificaciones_usuarios`
    FOREIGN KEY (`usuario_id`)
    REFERENCES `usuarios` (`id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;