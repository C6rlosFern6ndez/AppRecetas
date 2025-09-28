-- Insertar comentarios de ejemplo
INSERT INTO `comentarios` (`receta_id`, `usuario_id`, `comentario`) VALUES
(1, 2, '¡Esta receta está deliciosa!'),
(1, 3, 'Fácil de seguir, buen resultado'),
(2, 1, 'Necesita un poco más de sazón');

-- Ejemplos CRUD

-- Crear nuevo comentario
INSERT INTO `comentarios` (`receta_id`, `usuario_id`, `comentario`) VALUES (1, 4, 'Excelente postre');

-- Leer todos los comentarios
SELECT * FROM `comentarios`;

-- Leer comentario por ID
SELECT * FROM `comentarios` WHERE `id` = 1;

-- Actualizar comentario por ID
UPDATE `comentarios` SET `comentario` = '¡Esta receta es increíble!' WHERE `id` = 1;

-- Eliminar comentario por ID
DELETE FROM `comentarios` WHERE `id` = 1;