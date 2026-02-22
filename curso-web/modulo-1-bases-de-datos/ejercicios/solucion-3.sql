-- ============================================================
-- SOLUCIÓN EJERCICIO 3: Relaciones entre tablas
-- ============================================================

-- PASO 1: Tabla clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    ciudad VARCHAR(50)
);

-- PASO 2: Tabla pedidos con FOREIGN KEY
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    producto VARCHAR(100),
    cantidad INT DEFAULT 1,
    total DECIMAL(10,2),
    fecha DATE,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- PASO 3: Insertar clientes
INSERT INTO clientes (nombre, email, ciudad) VALUES
    ('Laura Méndez', 'laura@email.com', 'CDMX'),
    ('Roberto Silva', 'roberto@email.com', 'Monterrey'),
    ('Sofia Torres', 'sofia@email.com', 'Guadalajara'),
    ('Diego Ramírez', 'diego@email.com', 'CDMX');

-- PASO 4: Insertar pedidos
INSERT INTO pedidos (cliente_id, producto, cantidad, total, fecha) VALUES
    (1, 'Laptop HP', 1, 12500.00, '2026-01-15'),
    (1, 'Mouse Logitech', 2, 901.00, '2026-01-15'),
    (2, 'Monitor Samsung', 1, 5600.00, '2026-02-01'),
    (3, 'Teclado mecánico', 1, 1200.00, '2026-02-10'),
    (3, 'Cable HDMI', 3, 450.00, '2026-02-10'),
    (4, 'Laptop HP', 1, 12500.00, '2026-02-20');

-- PREGUNTA A: Nombre del cliente y productos
SELECT clientes.nombre, pedidos.producto, pedidos.total
FROM pedidos
JOIN clientes ON pedidos.cliente_id = clientes.id;

-- PREGUNTA B: Total gastado por cliente
SELECT clientes.nombre, SUM(pedidos.total) AS total_gastado
FROM pedidos
JOIN clientes ON pedidos.cliente_id = clientes.id
GROUP BY clientes.id, clientes.nombre;

-- PREGUNTA C: Pedidos de clientes de CDMX
SELECT clientes.nombre, pedidos.producto, pedidos.total
FROM pedidos
JOIN clientes ON pedidos.cliente_id = clientes.id
WHERE clientes.ciudad = 'CDMX';

-- PREGUNTA D: Cantidad de pedidos por cliente
SELECT clientes.nombre, COUNT(pedidos.id) AS numero_pedidos
FROM pedidos
JOIN clientes ON pedidos.cliente_id = clientes.id
GROUP BY clientes.id, clientes.nombre;
