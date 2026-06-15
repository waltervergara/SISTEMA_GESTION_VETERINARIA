

CREATE TABLE inventario(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    fecha_elaboracion DATE NOT NULL,
    vencimiento VARCHAR(100) NOT NULL,
    stock BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    precio BIGINT NOT NULL


);