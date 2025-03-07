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

-- Tabla de Datos Personales (Relacionada con Perfiles y Facultades)
CREATE TABLE datosest (
    id_perfiles INT PRIMARY KEY,
    nombres VARCHAR(50),
    apellidos VARCHAR(50),
    ndoc INT UNIQUE NOT NULL,
    id_facultad INT,
    id_rol INT,
    FOREIGN KEY (id_perfiles) REFERENCES perfiles(id_perfiles) ON DELETE CASCADE,
    FOREIGN KEY (id_facultad) REFERENCES facultades(id_facultad), 
    FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
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
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de Roles


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

CREATE TRIGGER audit_profile_update AFTER UPDATE ON perfiles
FOR EACH ROW
BEGIN
    INSERT INTO auditoria(id_perfiles, operacion, detalle, fecha)
    VALUES (
        OLD.id_perfiles, 
        'UPDATE', 
        CONCAT(
            'Se actualizó el perfil de ', OLD.nomperfil, 
            ' a ', NEW.nomperfil, 
            '. Cambio de contraseña realizado.'
        ), 
        NOW()
    );
END$$

DELIMITER ;


DELIMITER $$

CREATE TRIGGER audit_profile_delete AFTER DELETE ON perfiles
FOR EACH ROW
BEGIN

    INSERT INTO auditoria(id_perfiles, operacion, detalle, fecha)
    VALUES (OLD.id_perfiles, 'DELETE', CONCAT('Se eliminó el perfil de ', OLD.nomperfil), NOW());

END$$

DELIMITER ;

INSERT INTO roles(nombre_rol) VALUES ("Estudiante"), ("Profesor"), ("Administrador");

INSERT INTO tipo_permisos (nombre_permiso) VALUES ('Leer'), ('Escribir'), ('Eliminar');

INSERT INTO permisos (id_rol, id_tipo_permiso) VALUES 
(1, 1), -- Estudiante puede leer
(2, 1), -- Profesor puede leer
(2, 2), -- Profesor puede escribir
(3, 1), -- Administrador puede leer
(3, 2), -- Administrador puede escribir
(3, 3); -- Administrador puede eliminar

INSERT INTO facultades (nombre_facultad) VALUES ("Artes"),
("Ciencias"),
("Ciencias Agrarias"),
("Ciencias Económicas"),
("Ciencias Humanas"),
("Derecho"),
("Ciencias Políticas y Sociales"),
("Enfermería"),
("Ingeniería"),
("Medicina"),
("Medicina Veterinaria y Zootecnia"),
("Odontología");

DELIMITER //
CREATE PROCEDURE verificarUsuario(
    IN p_usuario VARCHAR(50), 
    IN p_contraseña VARCHAR(255), 
    OUT p_id_rol INT
)
BEGIN
    SELECT r.id_rol INTO p_id_rol
    FROM perfiles p
    JOIN datosest d ON p.id_perfiles = d.id_perfiles
    JOIN roles r ON d.id_rol = r.id_rol
    WHERE p.nomperfil = p_usuario AND p.password = p_contraseña;

    -- Si no encuentra un usuario devuelve -1 
    IF p_id_rol IS NULL THEN
        SET p_id_rol = -1;
    END IF;
END //
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE InsertarDatos()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE nombre VARCHAR(50);
    DECLARE apellido VARCHAR(50);
    DECLARE usuario VARCHAR(50);
    DECLARE base_usuario VARCHAR(50);
    DECLARE nuevo_usuario VARCHAR(50);
    DECLARE pass VARCHAR(10);
    DECLARE doc INT;
    DECLARE facultad INT;
    DECLARE rol INT;
    DECLARE perfil_id INT;
    DECLARE usuario_existe INT;

    WHILE i <= 10000 DO
        
        SET nombre = ELT(FLOOR(1 + (RAND() * 10)), 'Juan', 'María', 'Carlos', 'Ana', 'Luis', 'Elena', 'Pedro', 'Sofía', 'Miguel', 'Laura');
        SET apellido = ELT(FLOOR(1 + (RAND() * 10)), 'Pérez', 'Gómez', 'López', 'Torres', 'Fernández', 'Martínez', 'Rodríguez', 'Díaz', 'Sánchez', 'Jiménez');

       
        SET base_usuario = CONCAT(LOWER(nombre), '.', LOWER(apellido));
        SET nuevo_usuario = base_usuario;
        SET usuario_existe = 1; 

       
        WHILE usuario_existe > 0 DO
            SELECT COUNT(*) INTO usuario_existe FROM perfiles WHERE nomperfil = nuevo_usuario;
            IF usuario_existe > 0 THEN
                
                SET nuevo_usuario = CONCAT(base_usuario, FLOOR(1 + (RAND() * 99)));
            END IF;
        END WHILE;
        SET usuario = nuevo_usuario;

        
        SET pass = CONCAT(
            CHAR(FLOOR(65 + (RAND() * 26))), -- Letra mayúscula
            CHAR(FLOOR(97 + (RAND() * 26))), -- Letra minúscula
            CHAR(FLOOR(48 + (RAND() * 10))), -- Número
            CHAR(FLOOR(33 + (RAND() * 15))), -- Símbolo
            CHAR(FLOOR(65 + (RAND() * 26))), 
            CHAR(FLOOR(97 + (RAND() * 26))), 
            CHAR(FLOOR(48 + (RAND() * 10))), 
            CHAR(FLOOR(33 + (RAND() * 15))) 
        );

        
        INSERT INTO perfiles (nomperfil, password) VALUES (usuario, pass);
        SET perfil_id = LAST_INSERT_ID();

        
        SET doc = FLOOR(10000000 + (RAND() * 89999999));

       
        SET facultad = FLOOR(1 + (RAND() * 5)); -- Facultades del 1 al 5
        SET rol = FLOOR(1 + (RAND() * 3)); -- Roles del 1 al 3

        
        INSERT INTO datosest (id_perfiles, nombres, apellidos, ndoc, id_facultad, id_rol) 
        VALUES (perfil_id, nombre, apellido, doc, facultad, rol);
        
        SET i = i + 1;
    END WHILE;
END $$

DELIMITER ;

CALL InsertarDatos();
