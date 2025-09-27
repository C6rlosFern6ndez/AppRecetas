-- Insertar relaciones receta-ingrediente de ejemplo
INSERT INTO `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`) VALUES
(1, 1, '200g'),
(1, 2, '250g'),
(1, 3, '300ml');

-- Ejemplos CRUD

-- Crear nueva relación
INSERT INTO `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`) VALUES (1, 4, '4 unidades');

-- Leer todas las relaciones
SELECT * FROM `receta_ingredientes`;

-- Leer relación por ID
SELECT * FROM `receta_ingredientes` WHERE `receta_id` = 1 AND `ingrediente_id` = 1;

-- Actualizar relación
UPDATE `receta_ingredientes` SET `cantidad` = '250g' WHERE `receta_id` = 1 AND `ingrediente_id` = 1;

-- Eliminar relación
DELETE FROM `receta_ingredientes` WHERE `receta_id` = 1 AND `ingrediente_id` = 1;