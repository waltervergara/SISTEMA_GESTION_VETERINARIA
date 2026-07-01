CREATE TABLE Tratamiento (
    nombre VARCHAR(255) NOT NULL,
    diagnostico VARCHAR(255) NOT NULL,
    medicacion VARCHAR(255) NOT NULL,
    observacion VARCHAR(255) NOT NULL,
    fecha_revision DATETIME NOT NULL,
    codigo_microchip VARCHAR(255) NOT NULL,
    PRIMARY KEY (nombre)
);