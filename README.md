CREATE SCHEMA csia;
USE csia;

-- Tabla de Perfiles (Usuarios)
CREATE TABLE perfiles (
    id_perfiles INT PRIMARY KEY AUTO_INCREMENT,
    nomperfil VARCHAR(45) NOT NULL UNIQUE,
    password VARCHAR(45) NOT NULL
);

-- Tabla de Facultades (Nueva para normalización)
CREATE TABLE facultades (
    id_facultad INT PRIMARY KEY AUTO_INCREMENT,
    nombre_facultad VARCHAR(50) UNIQUE NOT NULL
);

-- Tabla de Datos Personales (Relacionada con Perfiles y Facultades)
CREATE TABLE datosest (
    id_perfiles INT PRIMARY KEY,
    nombres VARCHAR(50),
    apellidos VARCHAR(50),
    ndoc INT UNIQUE NOT NULL,
    id_facultad INT,
    FOREIGN KEY (id_perfiles) REFERENCES perfiles(id_perfiles) ON DELETE CASCADE,
    FOREIGN KEY (id_facultad) REFERENCES facultades(id_facultad)
);

-- Tabla de Materias (Relacionada con Facultades)
CREATE TABLE materias (
    id_materia INT PRIMARY KEY AUTO_INCREMENT,
    nombre_materia VARCHAR(100) NOT NULL UNIQUE,
    creditos INT CHECK (creditos > 0),
    id_facultad INT,
    FOREIGN KEY (id_facultad) REFERENCES facultades(id_facultad) ON DELETE SET NULL
);

-- Tabla de Calificaciones (Relacionada con Perfiles y Materias)
CREATE TABLE calificaciones (
    id_calificacion INT PRIMARY KEY AUTO_INCREMENT,
    id_perfiles INT,
    id_materia INT,
    nota DECIMAL(5,2) CHECK (nota BETWEEN 0 AND 10),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_perfiles) REFERENCES perfiles(id_perfiles) ON DELETE CASCADE,
    FOREIGN KEY (id_materia) REFERENCES materias(id_materia) ON DELETE CASCADE
);

-- Tabla de Auditoría (Relacionada con Perfiles)
CREATE TABLE auditoria (
    id_auditoria INT PRIMARY KEY AUTO_INCREMENT,
    id_perfiles INT,
    operacion VARCHAR(50),
    detalle TEXT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_perfiles) REFERENCES perfiles(id_perfiles) ON DELETE CASCADE
);

-- Tabla de Roles
CREATE TABLE roles (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

-- Tipos de permisos 
CREATE TABLE tipo_permisos ( 
    id_tipo_permiso INT PRIMARY KEY AUTO_INCREMENT, 
    nombre_permiso VARCHAR(60)
);

-- Tabla de Permisos (Relacionada con Roles y Tipos de Permisos)
CREATE TABLE permisos (
    id_permiso INT PRIMARY KEY AUTO_INCREMENT,
    id_rol INT,
    id_tipo_permiso INT,
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol) ON DELETE CASCADE,
    FOREIGN KEY (id_tipo_permiso) REFERENCES tipo_permisos(id_tipo_permiso) ON DELETE CASCADE
);

-- 1. Trigger para registrar en la auditoría cuando se inserta o actualiza una calificación

DELIMITER $$

CREATE TRIGGER audit_calification_insert AFTER INSERT ON calificaciones
FOR EACH ROW
BEGIN

    INSERT INTO auditoria(id_perfiles, operacion, detalle, fecha)
    VALUES (NEW.id_perfiles, 'INSERT', CONCAT('Se insertó una calificación para la materia ', (SELECT nombre_materia FROM materias WHERE id_materia = NEW.id_materia), ' con nota ', NEW.nota), NOW());
    
END$$
DELIMITER ;

DELIMITER $$

CREATE TRIGGER audit_calification_update AFTER UPDATE ON calificaciones
FOR EACH ROW
BEGIN

    INSERT INTO auditoria(id_perfiles, operacion, detalle, fecha)
    VALUES (NEW.id_perfiles, 'UPDATE', CONCAT('Se actualizó la calificación de la materia ', (SELECT nombre_materia FROM materias WHERE id_materia = NEW.id_materia), ' de ', OLD.nota, ' a ', NEW.nota), NOW());

END$$


DELIMITER ;

DELIMITER $$

CREATE TRIGGER audit_profile_delete BEFORE DELETE ON perfiles
FOR EACH ROW
BEGIN

    INSERT INTO auditoria(id_perfiles, operacion, detalle, fecha)
    VALUES (OLD.id_perfiles, 'DELETE', CONCAT('Se eliminó el perfil de ', OLD.nomperfil), NOW());

END$$

DELIMITER ;





