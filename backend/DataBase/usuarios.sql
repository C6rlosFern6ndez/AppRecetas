
-- Insertar varios usuarios de ejemplo
INSERT INTO `usuarios` (`nombre_usuario`, `email`, `contrasena`) VALUES
('juan_perez', 'juan.perez@email.com', 'hashed_password_1'),
('ana_garcia', 'ana.garcia@email.com', 'hashed_password_2'),
('chef_master', 'chef.master@email.com', 'hashed_password_3');


--CRUD
--Crear nuevo usuario
INSERT INTO `usuarios` (`nombre_usuario`, `email`, `contrasena`)
VALUES ('nuevo_usuario', 'nuevo.usuario@email.com', 'nueva_contrasena_hasheada');

--Leer todos los usuarios
SELECT * FROM `usuarios`;

--Leer un usuario por ID
SELECT * FROM `usuarios` WHERE `id` = 1;
--Leer un usuario por email
SELECT * FROM `usuarios` WHERE `email` = 'nuevo.usuario@email.com';

--Actualizar un usuario por ID
UPDATE `usuarios` SET `nombre_usuario` = 'nuevo_nombre', `email` = 'nuevo.email@email.com', `contrasena` = 'nueva_contrasena_hasheada' WHERE `id` = 1;

--Eliminar un usuario por ID
DELETE FROM `usuarios` WHERE `id` = 1;