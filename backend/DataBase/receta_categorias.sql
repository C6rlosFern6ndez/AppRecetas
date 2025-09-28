-- Insertar relaciones receta-categoría de ejemplo
INSERT INTO `receta_categorias` (`receta_id`, `categoria_id`) VALUES
(1, 1),
(2, 2),
(3, 3);

-- Ejemplos CRUD

-- Crear nueva relación
INSERT INTO `receta_categorias` (`receta_id`, `categoria_id`) VALUES (4, 4);

-- Leer todas las relaciones
SELECT * FROM `receta_categorias`;

-- Leer relación por ID
SELECT * FROM `receta_categorias` WHERE `receta_id` = 1 AND `categoria_id` = 1;

-- Actualizar relación
UPDATE `receta_categorias` SET `categoria_id` = 4 WHERE `receta_id` = 1 AND `categoria_id` = 1;

-- Eliminar relación
DELETE FROM `receta_categorias` WHERE `receta_id` = 1 AND `categoria_id` = 1;