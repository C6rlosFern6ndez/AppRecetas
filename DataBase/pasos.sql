-- Insertar pasos de ejemplo
INSERT INTO `pasos` (`receta_id`, `orden`, `descripcion`) VALUES
(1, 1, 'Precalentar el horno a 180°C'),
(1, 2, 'Mezclar los ingredientes secos'),
(1, 3, 'Verter la mezcla en un molde');

-- Ejemplos CRUD

-- Crear nuevo paso
INSERT INTO `pasos` (`receta_id`, `orden`, `descripcion`) VALUES (1, 4, 'Hornear durante 30 minutos');

-- Leer todos los pasos
SELECT * FROM `pasos`;

-- Leer paso por ID
SELECT * FROM `pasos` WHERE `id` = 1;

-- Actualizar paso por ID
UPDATE `pasos` SET `descripcion` = 'Precalentar el horno a 200°C' WHERE `id` = 1;

-- Eliminar paso por ID
DELETE FROM `pasos` WHERE `id` = 1;