SELECT * FROM recetas;

SELECT * FROM recetas WHERE id = 1;

-- Consultar detalles de una receta específica
SELECT
    r.id,
    r.titulo,
    r.descripcion,
    r.tiempo_preparacion,
    r.dificultad,
    r.porciones,
    u.nombre_usuario
FROM
    recetas r
    JOIN usuarios u ON r.usuario_id = u.id
WHERE
    r.id = 1;

-- Consultar categorías de una receta específica
SELECT
    c.nombre
FROM
    receta_categorias rc
    JOIN categorias c ON rc.categoria_id = c.id
WHERE
    rc.receta_id = 1;

-- Consultar ingredientes de una receta específica
SELECT
    i.nombre,
    ri.cantidad
FROM
    receta_ingredientes ri
    JOIN ingredientes i ON ri.ingrediente_id = i.id
WHERE
    ri.receta_id = 1;

-- Validar subconsulta para categorías
SELECT
    GROUP_CONCAT(c.nombre)
FROM
    categorias c
    JOIN receta_categorias rc ON c.id = rc.categoria_id
WHERE
    rc.receta_id = 1;

-- Validar subconsulta para ingredientes
SELECT
    GROUP_CONCAT(CONCAT(i.nombre, ' (', ri.cantidad, ')'))
FROM
    ingredientes i
    JOIN receta_ingredientes ri ON i.id = ri.ingrediente_id
WHERE
    ri.receta_id = 1;

-- Validar subconsulta para pasos
SELECT
    GROUP_CONCAT(p.orden, '. ', p.descripcion)
FROM
    pasos p
WHERE
    p.receta_id = 1;

-- Step 1: Insert the recipe
INSERT INTO
    `recetas` (
        `titulo`,
        `descripcion`,
        `tiempo_preparacion`,
        `dificultad`,
        `porciones`,
        `usuario_id`
    )
VALUES
    (
        'Tarta de Manzana',
        'Deliciosa tarta con manzanas frescas',
        90,
        'Media',
        6,
        1
    );

-- Step 2: Get the ID of the newly created recipe
SET
    @recipe_id = LAST_INSERT_ID();

-- Step 3: Insert categories for the recipe
INSERT INTO
    `receta_categorias` (`receta_id`, `categoria_id`)
VALUES
    (@recipe_id, 1),
    (@recipe_id, 3);

-- Step 4: Insert ingredients and quantities
INSERT INTO
    `receta_ingredientes` (`receta_id`, `ingrediente_id`, `cantidad`)
VALUES
    (@recipe_id, 1, '200g'),
    (@recipe_id, 4, '4 unidades');

-- Step 5: Insert preparation steps
INSERT INTO
    `pasos` (`receta_id`, `orden`, `descripcion`)
VALUES
    (@recipe_id, 1, 'Pelar y cortar las manzanas'),
    (@recipe_id, 2, 'Mezclar con azúcar y canela');

-- ELIMINAR RECETAS PROPIAS
-- Step 1: Validate ownership
SELECT
    id
FROM
    recetas
WHERE
    id = [recipe_id]
    AND usuario_id = [user_id];

-- Step 2: If the result is valid, proceed with deletion
DELETE FROM
    recetas
WHERE
    id = [recipe_id]
    AND usuario_id = [user_id];

SELECT
    r.id AS receta_id,
    r.titulo,
    r.descripcion,
    r.tiempo_preparacion,
    r.dificultad,
    r.porciones,
    u.nombre_usuario AS creador,
    (
        SELECT
            GROUP_CONCAT(c.nombre)
        FROM
            categorias c
            JOIN receta_categorias rc ON c.id = rc.categoria_id
        WHERE
            rc.receta_id = r.id
    ) AS categorias,
    (
        SELECT
            GROUP_CONCAT(CONCAT(i.nombre, ' (', ri.cantidad, ')'))
        FROM
            ingredientes i
            JOIN receta_ingredientes ri ON i.id = ri.ingrediente_id
        WHERE
            ri.receta_id = r.id
    ) AS ingredientes,
    (
        SELECT
            GROUP_CONCAT(p.orden, '. ', p.descripcion)
        FROM
            pasos p
        WHERE
            p.receta_id = r.id
    ) AS pasos
FROM
    recetas r
    JOIN usuarios u ON r.usuario_id = u.id
WHERE
    r.id = 1;