-- V1__Initial_Schema.sql
-- Script de migración inicial unificado (Basado en Tablas.sql)

-- 1. Tablas de Roles
CREATE TABLE `roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(20) NOT NULL UNIQUE,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

-- Inserts de Roles (de Tablas.sql)
INSERT INTO `roles` (`id`, `nombre`) VALUES (1, 'USER');
INSERT INTO `roles` (`id`, `nombre`) VALUES (2, 'SUPERADMIN');
INSERT INTO `roles` (`id`, `nombre`) VALUES (3, 'INVITADO');
INSERT INTO `roles` (`id`, `nombre`) VALUES (4, 'ADMIN');

-- 2. Tablas de Usuarios (con rol_id FK, sin tabla usuario_roles)
CREATE TABLE `usuarios` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre_usuario` VARCHAR(50) NOT NULL UNIQUE,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `contrasena` VARCHAR(255) NOT NULL,
  `url_foto_perfil` VARCHAR(255) NULL,
  `fecha_registro` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  
  -- Columna de Rol Único: Mantenemos el diseño de Tablas.sql
  `rol_id` INT NOT NULL DEFAULT 1, 
  
  PRIMARY KEY (`id`),
  FOREIGN KEY (`rol_id`) REFERENCES `roles` (`id`) ON DELETE RESTRICT 
) ENGINE=InnoDB;

-- 3. Seguidores
CREATE TABLE `seguidores` (
  `seguidor_id` INT NOT NULL,
  `seguido_id` INT NOT NULL,
  PRIMARY KEY (`seguidor_id`, `seguido_id`),
  FOREIGN KEY (`seguidor_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`seguido_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 4. Recetas
CREATE TABLE `recetas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(100) NOT NULL,
  `descripcion` TEXT NOT NULL,
  `tiempo_preparacion` INT NOT NULL,
  `dificultad` ENUM('Fácil', 'Media', 'Difícil') NOT NULL,
  `porciones` INT NOT NULL,
  `url_imagen` VARCHAR(255) NULL,
  `usuario_id` INT NOT NULL,
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 5. Pasos
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

-- 6. Categorías
CREATE TABLE `categorias` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC)
) ENGINE=InnoDB;

-- Inserts de Categorías (de Tablas.sql)
INSERT INTO `categorias` (`nombre`) VALUES
('Postres'),
('Comida Saludable'),
('Vegetariano'),
('Carnes'),
('Pescados y Mariscos'),
('Pasta'),
('Guarniciones'),
('Desayunos');

-- 7. Receta Categorías
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

-- 8. Ingredientes
CREATE TABLE `ingredientes` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `nombre` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `nombre_UNIQUE` (`nombre` ASC)
) ENGINE=InnoDB;

-- 9. Receta Ingredientes
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

-- 10. Comentarios
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

-- 11. Calificaciones
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

-- 12. Likes (usando el nombre de Tablas.sql)
CREATE TABLE `recetas_likes` (
  `usuario_id` INT NOT NULL,
  `receta_id` INT NOT NULL,
  PRIMARY KEY (`usuario_id`, `receta_id`),
  FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 13. Notificaciones
CREATE TABLE `notificaciones` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `usuario_id` INT NOT NULL,
  `tipo` ENUM('NUEVO_SEGUIDOR', 'ME_GUSTA_RECETA', 'NUEVO_COMENTARIO'),
  `emisor_id` INT,
  `receta_id` INT,
  `mensaje` TEXT NULL,
  `leida` BOOLEAN DEFAULT FALSE,
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`emisor_id`) REFERENCES `usuarios` (`id`) ON DELETE SET NULL,
  FOREIGN KEY (`receta_id`) REFERENCES `recetas` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB;
