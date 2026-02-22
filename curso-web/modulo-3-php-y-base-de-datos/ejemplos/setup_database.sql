-- ============================================================
-- Script de setup: ejecuta esto primero para crear la BD
-- Desde terminal: mysql -u root < setup_database.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS tienda;
USE tienda;

DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS clientes;

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    cantidad_en_stock INT DEFAULT 0,
    categoria VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    ciudad VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT DEFAULT 1,
    total DECIMAL(10,2) NOT NULL,
    fecha DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id)
);

INSERT INTO productos (nombre, precio, cantidad_en_stock, categoria) VALUES
    ('Laptop HP', 12500.00, 10, 'Electrónica'),
    ('Mouse Logitech', 450.50, 25, 'Accesorios'),
    ('Teclado mecánico', 1200.00, 15, 'Accesorios'),
    ('Monitor Samsung', 5600.00, 8, 'Electrónica'),
    ('Cable HDMI', 150.00, 50, 'Cables');

INSERT INTO clientes (nombre, email, ciudad) VALUES
    ('Laura Méndez', 'laura@email.com', 'CDMX'),
    ('Roberto Silva', 'roberto@email.com', 'Monterrey'),
    ('Sofia Torres', 'sofia@email.com', 'Guadalajara');

SELECT 'Base de datos configurada correctamente' AS mensaje;
