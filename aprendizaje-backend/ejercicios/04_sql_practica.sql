-- M5 - SQL de practica
-- Puedes correr estas consultas en SQLite.

-- 1) Crea tabla products
CREATE TABLE IF NOT EXISTS products (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  name TEXT NOT NULL,
  price REAL NOT NULL,
  stock INTEGER NOT NULL
);

-- 2) Inserta 4 productos
INSERT INTO products (name, price, stock) VALUES
  ('Laptop', 1200, 5),
  ('Mouse', 25, 50),
  ('Keyboard', 70, 25),
  ('Monitor', 300, 10);

-- 3) Lista productos con stock mayor a 10
SELECT id, name, price, stock
FROM products
WHERE stock > 10
ORDER BY price DESC;

-- 4) Saca los 2 productos mas caros
SELECT id, name, price
FROM products
ORDER BY price DESC
LIMIT 2;

-- 5) Actualiza stock de Mouse a 45
UPDATE products
SET stock = 45
WHERE name = 'Mouse';

-- 6) Borra productos con stock = 0
DELETE FROM products
WHERE stock = 0;

-- Reto extra:
-- Escribe una consulta que calcule el valor total de inventario:
-- formula: SUM(price * stock)
