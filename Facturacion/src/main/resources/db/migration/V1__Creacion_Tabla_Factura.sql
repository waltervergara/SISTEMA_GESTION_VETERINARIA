CREATE TABLE Factura (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_factura VARCHAR(255) NOT NULL,
    detalles VARCHAR(255) NOT NULL,
    fecha_emision DATETIME NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    run_propietario VARCHAR(13) NOT NULL
);