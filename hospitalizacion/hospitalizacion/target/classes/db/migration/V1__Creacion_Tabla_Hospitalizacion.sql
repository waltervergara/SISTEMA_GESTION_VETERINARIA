CREATE TABLE hospitalizados(
    codigo_hospitalizacion VARCHAR(15) NOT NULL PRIMARY KEY,
    hora_monitoreo DATETIME NOT NULL,
    sala VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    codigo_microchip VARCHAR(15) NOT NULL
    
);