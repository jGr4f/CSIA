CREATE TABLE `csia`.`perfiles` (
  `id_perfiles` INT NOT NULL AUTO_INCREMENT,
  `nomperfil` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_perfiles`));

CREATE TABLE datosest (
    id_perfiles INT PRIMARY KEY AUTO_INCREMENT,
    nombres VARCHAR(50),
    apellidos VARCHAR(50),
    ndoc INT,
    facultad VARCHAR(50),
    FOREIGN KEY (id_perfiles) REFERENCES perfiles(id_perfiles) ON DELETE CASCADE
);
