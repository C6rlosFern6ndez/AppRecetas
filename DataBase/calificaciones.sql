-- Insertar calificaciones de ejemplo
INSERT INTO `calificaciones` (`receta_id`, `usuario_id`, `puntuacion`) VALUES
(1, 2, 5),
(1, 3, 4),
(2, 1, 3);

-- Ejemplos CRUD

-- Crear nueva calificación
INSERT INTO `calificaciones` (`receta_id`, `usuario_id`, `puntuacion`) VALUES (1, 4, 5);

-- Leer todas las calificaciones
SELECT * FROM `calificaciones`;

-- Leer calificación por ID
SELECT * FROM `calificaciones` WHERE `id` = 1;

-- Actualizar calificación por ID
UPDATE `calificaciones` SET `puntuacion` = 4 WHERE `id` = 1;

-- Eliminar calificación por ID
DELETE FROM `calificaciones` WHERE `id` = 1;