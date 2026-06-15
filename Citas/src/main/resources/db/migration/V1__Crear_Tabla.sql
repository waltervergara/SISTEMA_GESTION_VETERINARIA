CREATE TABLE citas_medicas(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_consulta VARCHAR(255) NOT NULL UNIQUE,
    fecha_hora DATETIME NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    estado VARCHAR(255) NOT NULL,
    observaciones VARCHAR(500),
    codigo_microchip Varchar(15) NOT NULL,
    run_propietario VARCHAR(13) NOT NULL,
    run_empleado VARCHAR(13) NOT NULL
);