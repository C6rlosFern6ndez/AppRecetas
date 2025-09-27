-- Insertar recetas de ejemplo
INSERT INTO `recetas` (`titulo`, `descripcion`, `tiempo_preparacion`, `dificultad`, `porciones`, `usuario_id`) VALUES
('Pastel de Chocolate', 'Un delicioso pastel de chocolate', 60, 'Fácil', 4, 1),
('Espagueti a la Carbonara', 'Receta italiana tradicional', 30, 'Media', 2, 2),
('Ensalada César', 'Ensalada con pollo y pan tostado', 25, 'Fácil', 1, 3);

-- Ejemplos CRUD

-- Crear nueva receta
INSERT INTO `recetas` (`titulo`, `descripcion`, `tiempo_preparacion`, `dificultad`, `porciones`, `usuario_id`) 
VALUES ('Sopa de Tomate', 'Sopa clásica de tomate con hierbas', 45, 'Fácil', 6, 1);

-- Leer todas las recetas
SELECT * FROM `recetas`;

-- Leer receta por ID
SELECT * FROM `recetas` WHERE `id` = 1;

-- Actualizar receta por ID
UPDATE `recetas` SET `titulo` = 'Pastel de Chocolate Mejorado' WHERE `id` = 1;

-- Eliminar receta por ID
DELETE FROM `recetas` WHERE `id` = 1;