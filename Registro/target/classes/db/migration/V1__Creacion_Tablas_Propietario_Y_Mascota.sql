CREATE TABLE propietario (
    run_propietario VARCHAR(13) NOT NULL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL,
    telefono VARCHAR(12) NOT NULL
);

CREATE TABLE mascota (
    codigo_microchip VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    edad INT NOT NULL,
    año_nacimiento INT NOT NULL,
    especie VARCHAR(255) NOT NULL,
    raza VARCHAR(255) NOT NULL,
    run_propietario VARCHAR(13),
    CONSTRAINT fk_mascota_propietario FOREIGN KEY (run_propietario) REFERENCES propietario(run_propietario)
);