CREATE TABLE Lab_orden(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_orden VARCHAR(255) NOT NULL UNIQUE,
    fecha_pedido DATETIME NOT NULL,
    tipo_examen VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    codigo_microchip VARCHAR(15) NOT NULL
    
);