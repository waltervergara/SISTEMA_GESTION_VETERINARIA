-- Tabla de Roles (Ej: ROLE_VET, ROLE_ASSISTANT, ROLE_OWNER)
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de Usuarios (Doctores, Asistentes, Dueños)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE
);

-- Tabla pivote para la relación Muchos a Muchos (Un usuario puede tener varios roles)
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Insertamos los roles base para la veterinaria
INSERT INTO roles (name) VALUES ('ROLE_VET');
INSERT INTO roles (name) VALUES ('ROLE_ASSISTANT');
INSERT INTO roles (name) VALUES ('ROLE_OWNER');