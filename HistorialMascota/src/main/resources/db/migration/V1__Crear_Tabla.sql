CREATE TABLE historiales (
    id_historial BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_microchip VARCHAR(255) NOT NULL UNIQUE,
    fecha_creacion_historial DATETIME NOT NULL,
    observaciones_generales VARCHAR(1000) NOT NULL
);