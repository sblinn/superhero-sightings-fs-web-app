DROP DATABASE IF EXISTS SuperheroSightingsDB;
CREATE DATABASE SuperheroSightingsDB;

USE SuperheroSightingsDB;

CREATE TABLE Superhero (
	id INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(100) NOT NULL
);

CREATE TABLE Superpower (
	id INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL
);

CREATE TABLE Superhero_Superpower (
	superhero_id INT, -- FK
    superpower_id INT, -- FK
    PRIMARY KEY (superhero_id, superpower_id)
);

CREATE TABLE Sighting (
	id INT PRIMARY KEY AUTO_INCREMENT,
    location_id INT NOT NULL, -- FK
    superhero_id INT NOT NULL, -- FK
    `date` DATETIME NOT NULL
);

CREATE TABLE Location (
	id INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    street_address VARCHAR(50), -- CAN BE NULL
    city VARCHAR(50) NOT NULL,
    state CHAR(2), -- CAN BE NULL
    country CHAR(2) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    `description` VARCHAR(100)
);

CREATE TABLE `Organization` (
	id INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(50) NOT NULL,
    `description` VARCHAR(100), -- CAN BE NULL
    street_address VARCHAR(50), -- CAN BE NULL
    city VARCHAR(50) NOT NULL,
    country CHAR(2) NOT NULL
);

CREATE TABLE Organization_Superhero (
	superhero_id INT NOT NULL, -- FK
    org_id INT NOT NULL, -- FK
    PRIMARY KEY (superhero_id, org_id)
);


-- SET FOREIGN KEYS

ALTER TABLE Organization_Superhero
	ADD CONSTRAINT fk_Organization_Superhero
		FOREIGN KEY (org_id)
        REFERENCES `Organization`(id),
	ADD CONSTRAINT fk_Superhero_Organization
		FOREIGN KEY (superhero_id)
        REFERENCES Superhero(id);

ALTER TABLE Superhero_Superpower 
	ADD CONSTRAINT fk_Superhero_Superpower
		FOREIGN KEY (superhero_id)
        REFERENCES Superhero(id),
	ADD CONSTRAINT fk_Superpower_Superhero
		FOREIGN KEY (superpower_id)
        REFERENCES Superpower(id);

ALTER TABLE Sighting 
	ADD CONSTRAINT fk_Sighting_Superhero
		FOREIGN KEY (superhero_id)
        REFERENCES Superhero(id),
	ADD CONSTRAINT fk_Sighting_Location
		FOREIGN KEY (location_id)
        REFERENCES Location(id);
