-- V1__Initial_Schema.sql
-- Script de migración inicial unificado (Basado en Tablas.sql)

-- 1. Tablas de Roles
CREATE TABLE roles (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(20) NOT NULL UNIQUE,
  PRIMARY KEY (id)
);

-- Inserts de Roles (de Tablas.sql)
INSERT INTO roles (nombre) VALUES ('USER'), ('SUPERADMIN'), ('INVITADO'), ('ADMIN');

-- 2. Tablas de Usuarios (con rol_id FK, sin tabla usuario_roles)
CREATE TABLE usuarios (
  id INT NOT NULL AUTO_INCREMENT,
  nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  contrasena VARCHAR(255) NOT NULL,
  url_foto_perfil VARCHAR(255) NULL,
  delete_hash_perfil VARCHAR(255) NULL, -- Nueva columna para el hash de eliminación de la imagen de perfil
  fecha_registro TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

  -- Columna de Rol Único: Mantenemos el diseño de Tablas.sql
  rol_id INT NOT NULL DEFAULT 1,

  PRIMARY KEY (id),
  FOREIGN KEY (rol_id) REFERENCES roles (id) ON DELETE RESTRICT
);

-- 3. Seguidores
CREATE TABLE `seguidores` (
  `seguidor_id` INT NOT NULL,
  `seguido_id` INT NOT NULL,
  PRIMARY KEY (`seguidor_id`, `seguido_id`),
  FOREIGN KEY (`seguidor_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`seguido_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
);

-- 4. Recetas
CREATE TABLE `recetas` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `titulo` VARCHAR(100) NOT NULL,
  `descripcion` TEXT NOT NULL,
  `tiempo_preparacion` INT NOT NULL,
  `dificultad` ENUM('Fácil', 'Media', 'Difícil') NOT NULL,
  `porciones` INT NOT NULL,
  `url_imagen` VARCHAR(255) NULL,
  `delete_hash_imagen` VARCHAR(255) NULL, -- Nueva columna para el hash de eliminación de la imagen de la receta
  `usuario_id` INT NOT NULL,
  `fecha_creacion` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`id`) ON DELETE CASCADE
);

-- 5. Pasos
CREATE TABLE pasos (
  id INT NOT NULL AUTO_INCREMENT,
  receta_id INT NOT NULL,
  orden INT NOT NULL,
  descripcion TEXT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE CASCADE
);

-- 6. Categorías
CREATE TABLE categorias (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);

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
CREATE TABLE receta_categorias (
  receta_id INT NOT NULL,
  categoria_id INT NOT NULL,
  PRIMARY KEY (receta_id, categoria_id),
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE CASCADE,
  FOREIGN KEY (categoria_id) REFERENCES categorias (id) ON DELETE CASCADE
);

-- 8. Ingredientes
CREATE TABLE ingredientes (
  id INT NOT NULL AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  PRIMARY KEY (id)
);

-- 9. Receta Ingredientes
CREATE TABLE receta_ingredientes (
  receta_id INT NOT NULL,
  ingrediente_id INT NOT NULL,
  cantidad VARCHAR(50) NULL,
  PRIMARY KEY (receta_id, ingrediente_id),
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE CASCADE,
  FOREIGN KEY (ingrediente_id) REFERENCES ingredientes (id) ON DELETE CASCADE
);

-- 10. Comentarios
CREATE TABLE comentarios (
  id INT NOT NULL AUTO_INCREMENT,
  receta_id INT NOT NULL,
  usuario_id INT NOT NULL,
  comentario TEXT NOT NULL,
  fecha_comentario TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE CASCADE,
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- 11. Calificaciones
CREATE TABLE calificaciones (
  id INT NOT NULL AUTO_INCREMENT,
  receta_id INT NOT NULL,
  usuario_id INT NOT NULL,
  puntuacion INT NOT NULL,
  fecha_calificacion TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE CASCADE,
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
);

-- 12. Likes (usando el nombre de Tablas.sql)
CREATE TABLE recetas_likes (
  usuario_id INT NOT NULL,
  receta_id INT NOT NULL,
  PRIMARY KEY (usuario_id, receta_id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE CASCADE
);

-- 13. Notificaciones
CREATE TABLE notificaciones (
  id INT NOT NULL AUTO_INCREMENT,
  usuario_id INT NOT NULL,
  tipo ENUM('NUEVO_SEGUIDOR', 'ME_GUSTA_RECETA', 'NUEVO_COMENTARIO'),
  emisor_id INT,
  receta_id INT,
  mensaje TEXT NULL,
  leida BOOLEAN DEFAULT FALSE,
  fecha_creacion TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE,
  FOREIGN KEY (emisor_id) REFERENCES usuarios (id) ON DELETE SET NULL,
  FOREIGN KEY (receta_id) REFERENCES recetas (id) ON DELETE SET NULL
);

-- ============================================================================
-- DATOS DE PRUEBA PARA TESTING
-- ============================================================================


-- Insertar ingredientes de prueba
INSERT INTO `ingredientes` (`id`, `nombre`) VALUES
(1, 'Espinacas'),
(2, 'Garbanzos'),
(3, 'Aceite de oliva'),
(4, 'Ajo'),
(5, 'Pan'),
(6, 'Huevo'),
(7, 'Harina'),
(8, 'Leche'),
(9, 'Azúcar'),
(10, 'Mantequilla'),
(11, 'Pollo'),
(12, 'Arroz'),
(13, 'Tomate'),
(14, 'Cebolla'),
(15, 'Pimiento');

-- Insertar recetas de prueba
INSERT INTO `recetas` (`id`, `titulo`, `descripcion`, `tiempo_preparacion`, `dificultad`, `porciones`, `url_imagen`, `delete_hash_imagen`, `usuario_id`, `fecha_creacion`) VALUES
(1, 'Espinacas con Garbanzos', 'Delicioso plato tradicional español con espinacas frescas y garbanzos cocidos, salteados con ajo y aceite de oliva. Un plato vegetariano lleno de sabor y nutrientes.', 25, 'Fácil', 4, 'https://i.ibb.co/sp99GBz0/Verduras-Espinacas-con-garbanzos.jpg', 'test_delete_hash_recipe_1', 1, NOW()),
(2, 'Tortilla Española', 'La clásica tortilla de patatas española, jugosa por dentro y dorada por fuera. Un plato versátil perfecto para cualquier comida del día.', 30, 'Media', 4, NULL, NULL, 1, NOW()),
(3, 'Paella Valenciana', 'Auténtica paella valenciana con pollo, conejo y verduras frescas. El plato estrella de la gastronomía española.', 60, 'Difícil', 6, NULL, NULL, 2, NOW()),
(4, 'Crema Catalana', 'Postre tradicional catalán con una textura cremosa y una capa de azúcar caramelizado crujiente. Un final perfecto para cualquier comida.', 15, 'Fácil', 6, NULL, NULL, 2, NOW());

-- Insertar pasos para las recetas
INSERT INTO `pasos` (`id`, `receta_id`, `orden`, `descripcion`) VALUES
-- Pasos para Espinacas con Garbanzos
(1, 1, 1, 'Lavar bien las espinacas frescas y escurrirlas completamente.'),
(2, 1, 2, 'Pelar y picar finamente los dientes de ajo.'),
(3, 1, 3, 'Calentar aceite de oliva en una sartén grande a fuego medio.'),
(4, 1, 4, 'Dorar el pan en el aceite hasta que esté crujiente y reservar.'),
(5, 1, 5, 'En el mismo aceite, sofreír el ajo picado hasta que esté dorado.'),
(6, 1, 6, 'Añadir las espinacas y remover hasta que se reduzcan.'),
(7, 1, 7, 'Incorporar los garbanzos cocidos y mezclar bien.'),
(8, 1, 8, 'Servir caliente acompañado del pan frito por encima.'),

-- Pasos para Tortilla Española
(9, 2, 1, 'Pelar y cortar las patatas en rodajas finas.'),
(10, 2, 2, 'Batir los huevos en un bol grande.'),
(11, 2, 3, 'Freír las patatas hasta que estén tiernas.'),
(12, 2, 4, 'Mezclar las patatas con los huevos batidos.'),
(13, 2, 5, 'Cuajar la tortilla en la sartén hasta que esté dorada por ambos lados.'),

-- Pasos para Paella Valenciana
(14, 3, 1, 'Preparar el caldo con pollo y verduras.'),
(15, 3, 2, 'Sofreír el pollo y el conejo en la paellera.'),
(16, 3, 3, 'Añadir las verduras y el arroz.'),
(17, 3, 4, 'Incorporar el caldo caliente y dejar cocer.'),
(18, 3, 5, 'Dejar reposar antes de servir.'),

-- Pasos para Crema Catalana
(19, 4, 1, 'Calentar la leche con la canela y la piel de limón.'),
(20, 4, 2, 'Mezclar las yemas con el azúcar y la maicena.'),
(21, 4, 3, 'Incorporar la leche caliente a la mezcla de yemas.'),
(22, 4, 4, 'Cocinar hasta que espese.'),
(23, 4, 5, 'Enfriar y caramelizar el azúcar antes de servir.');

-- Asociar recetas con categorías
INSERT INTO `receta_categorias` (`receta_id`, `categoria_id`) VALUES
(1, 3), -- Espinacas con Garbanzos -> Vegetariano
(1, 2), -- Espinacas con Garbanzos -> Comida Saludable
(2, 3), -- Tortilla Española -> Vegetariano
(2, 8), -- Tortilla Española -> Desayunos
(3, 4), -- Paella Valenciana -> Carnes
(3, 5), -- Paella Valenciana -> Pescados y Mariscos
(4, 1); -- Crema Catalana -> Postres

-- Asociar ingredientes con recetas (Espinacas con Garbanzos)
INSERT INTO `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`) VALUES
(1, 1, '500g'), -- Espinacas
(1, 2, '400g'), -- Garbanzos
(1, 3, '50ml'), -- Aceite de oliva
(1, 4, '3 dientes'), -- Ajo
(1, 5, '2 rebanadas'); -- Pan

-- Asociar ingredientes con recetas (Tortilla Española)
INSERT INTO `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`) VALUES
(2, 6, '6 unidades'), -- Huevos
(2, 11, '200g'), -- Pollo (opcional)
(2, 3, '100ml'), -- Aceite de oliva
(2, 14, '1 unidad'); -- Cebolla

-- Asociar ingredientes con recetas (Paella Valenciana)
INSERT INTO `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`) VALUES
(3, 11, '300g'), -- Pollo
(3, 12, '400g'), -- Arroz
(3, 13, '2 unidades'), -- Tomate
(3, 14, '1 unidad'), -- Cebolla
(3, 15, '1 unidad'), -- Pimiento
(3, 2, '200g'); -- Garbanzos

-- Asociar ingredientes con recetas (Crema Catalana)
INSERT INTO `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`) VALUES
(4, 8, '500ml'), -- Leche
(4, 9, '100g'), -- Azúcar
(4, 6, '4 unidades'), -- Huevos
(4, 7, '30g'), -- Harina
(4, 10, '20g'); -- Mantequilla

-- Insertar comentarios de prueba
INSERT INTO `comentarios` (`id`, `receta_id`, `usuario_id`, `comentario`, `fecha_comentario`) VALUES
(1, 1, 2, '¡Excelente receta! Me salió perfecta siguiendo los pasos.', NOW()),
(2, 1, 3, 'Muy fácil de hacer y deliciosa. La repetiré seguro.', NOW()),
(3, 2, 1, 'La tortilla quedó jugosa como debe ser. ¡Gracias por la receta!', NOW()),
(4, 3, 2, 'La paella es mi plato favorito. Esta receta es auténtica.', NOW()),
(5, 4, 1, 'El postre estaba increíble. El contraste del azúcar caramelizado es perfecto.', NOW());

-- Insertar calificaciones de prueba
INSERT INTO `calificaciones` (`id`, `receta_id`, `usuario_id`, `puntuacion`, `fecha_calificacion`) VALUES
(1, 1, 2, 5, NOW()),
(2, 1, 3, 4, NOW()),
(3, 2, 1, 5, NOW()),
(4, 3, 2, 4, NOW()),
(5, 4, 1, 5, NOW());

-- Insertar likes de prueba
INSERT INTO `recetas_likes` (`usuario_id`, `receta_id`) VALUES
(2, 1), -- food_lover da like a Espinacas con Garbanzos
(3, 1), -- admin_user da like a Espinacas con Garbanzos
(1, 2), -- chef_master da like a Tortilla Española
(2, 3), -- food_lover da like a Paella Valenciana
(1, 4); -- chef_master da like a Crema Catalana

-- Insertar seguidores de prueba
INSERT INTO `seguidores` (`seguidor_id`, `seguido_id`) VALUES
(2, 1), -- food_lover sigue a chef_master
(3, 1), -- admin_user sigue a chef_master
(1, 2), -- chef_master sigue a food_lover
(3, 2), -- admin_user sigue a food_lover
(1, 3); -- chef_master sigue a admin_user

-- Insertar notificaciones de prueba
INSERT INTO `notificaciones` (`id`, `usuario_id`, `tipo`, `emisor_id`, `receta_id`, `mensaje`, `leida`, `fecha_creacion`) VALUES
(1, 1, 'ME_GUSTA_RECETA', 2, 1, 'A food_lover le gusta tu receta Espinacas con Garbanzos', FALSE, NOW()),
(2, 1, 'ME_GUSTA_RECETA', 3, 1, 'A admin_user le gusta tu receta Espinacas con Garbanzos', FALSE, NOW()),
(3, 1, 'NUEVO_COMENTARIO', 2, 1, 'food_lover ha comentado tu receta Espinacas con Garbanzos', FALSE, NOW()),
(4, 2, 'NUEVO_SEGUIDOR', 1, NULL, 'chef_master te está siguiendo', FALSE, NOW()),
(5, 1, 'NUEVO_COMENTARIO', 3, 2, 'admin_user ha comentado tu receta Tortilla Española', FALSE, NOW());

-- Crea las tablas necesarias para Spring Session JDBC (compatible con H2)

CREATE TABLE SPRING_SESSION (
    SESSION_ID CHAR(36) NOT NULL,
    CREATION_TIME BIGINT NOT NULL,
    LAST_ACCESS_TIME BIGINT NOT NULL,
    MAX_INACTIVE_INTERVAL INT NOT NULL,
    EXPIRY_TIME BIGINT NOT NULL,
    PRINCIPAL_NAME VARCHAR(100),
    SESSION_ATTRIBUTES BLOB NOT NULL,
    PRIMARY KEY (SESSION_ID)
);

CREATE INDEX SPRING_SESSION_IX1 ON SPRING_SESSION (EXPIRY_TIME);
CREATE INDEX SPRING_SESSION_IX2 ON SPRING_SESSION (PRINCIPAL_NAME);

CREATE TABLE SPRING_SESSION_ATTRIBUTES (
    SESSION_ID CHAR(36) NOT NULL,
    ATTRIBUTE_NAME VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES BLOB NOT NULL,
    PRIMARY KEY (SESSION_ID, ATTRIBUTE_NAME),
    FOREIGN KEY (SESSION_ID) REFERENCES SPRING_SESSION(SESSION_ID) ON DELETE CASCADE
);
