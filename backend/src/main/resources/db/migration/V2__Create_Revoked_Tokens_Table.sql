-- V2__Create_Revoked_Tokens_Table.sql
-- Script de migración para crear la tabla de tokens revocados.

CREATE TABLE revoked_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  token VARCHAR(500) NOT NULL UNIQUE,
  expiry_date TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);

-- Índice para optimizar la búsqueda por token
CREATE INDEX idx_revoked_token_token ON revoked_tokens (token);

-- Índice para optimizar la limpieza de tokens expirados
CREATE INDEX idx_revoked_token_expiry_date ON revoked_tokens (expiry_date);
