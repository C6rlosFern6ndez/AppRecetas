-- Insertar ingredientes de ejemplo
INSERT INTO `ingredientes` (`nombre`) VALUES
('Azúcar'),
('Harina'),
('Leche');

-- Ejemplos CRUD

-- Crear nuevo ingrediente
INSERT INTO `ingredientes` (`nombre`) VALUES ('Huevos');

-- Leer todos los ingredientes
SELECT * FROM `ingredientes`;

-- Leer ingrediente por ID
SELECT * FROM `ingredientes` WHERE `id` = 1;

-- Actualizar ingrediente por ID
UPDATE `ingredientes` SET `nombre` = 'Azúcar Blanca' WHERE `id` = 1;

-- Eliminar ingrediente por ID
DELETE FROM `ingredientes` WHERE `id` = 1;