-- Insertar categorías de ejemplo
INSERT INTO `categorias` (`nombre`) VALUES
('Postres'),
('Platos Principales'),
('Ensaladas');

-- Ejemplos CRUD

-- Crear nueva categoría
INSERT INTO `categorias` (`nombre`) VALUES ('Sopas');

-- Leer todas las categorías
SELECT * FROM `categorias`;

-- Leer categoría por ID
SELECT * FROM `categorias` WHERE `id` = 1;

-- Actualizar categoría por ID
UPDATE `categorias` SET `nombre` = 'Postres Dulces' WHERE `id` = 1;

-- Eliminar categoría por ID
DELETE FROM `categorias` WHERE `id` = 1;