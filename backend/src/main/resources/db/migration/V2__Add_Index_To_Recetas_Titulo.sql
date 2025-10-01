-- V2__Add_Index_To_Recetas_Titulo.sql
-- Añade un índice a la columna 'titulo' de la tabla 'recetas' para mejorar el rendimiento de búsqueda.

ALTER TABLE `recetas`
ADD INDEX `idx_recetas_titulo` (`titulo`);
