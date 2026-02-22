-- ============================================================
-- SOLUCIÓN EJERCICIO 1: Crear tu primera base de datos y tablas
-- ============================================================

-- PASO 1: Crear la base de datos
CREATE DATABASE tienda;

-- PASO 2: Usar la base de datos
USE tienda;

-- PASO 3: Crear la tabla productos
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2),
    cantidad_en_stock INT DEFAULT 0,
    categoria VARCHAR(50)
);

-- PASO 4: Insertar los 5 productos
INSERT INTO productos (nombre, precio, cantidad_en_stock, categoria) VALUES
    ('Laptop HP', 12500.00, 10, 'Electrónica'),
    ('Mouse Logitech', 450.50, 25, 'Accesorios'),
    ('Teclado mecánico', 1200.00, 15, 'Accesorios'),
    ('Monitor Samsung', 5600.00, 8, 'Electrónica'),
    ('Cable HDMI', 150.00, 50, 'Cables');

-- PASO 5: Mostrar todos los productos
SELECT * FROM productos;
