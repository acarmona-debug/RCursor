-- ============================================================
-- EJERCICIO 3: Relaciones entre tablas (JOIN)
-- ============================================================
-- ESCENARIO: Una tienda online con clientes y sus pedidos
-- ============================================================

-- PASO 1: Crea una tabla "clientes" con:
--   - id: entero, autoincremento, clave primaria
--   - nombre: texto de máximo 100 caracteres, obligatorio
--   - email: texto de máximo 100 caracteres, obligatorio y único
--   - ciudad: texto de máximo 50 caracteres
-- TU CÓDIGO AQUÍ:



-- PASO 2: Crea una tabla "pedidos" con:
--   - id: entero, autoincremento, clave primaria
--   - cliente_id: entero, obligatorio (referencia a clientes.id)
--   - producto: texto de máximo 100 caracteres
--   - cantidad: entero, por defecto 1
--   - total: decimal(10,2)
--   - fecha: DATE
--   - FOREIGN KEY de cliente_id hacia clientes(id)
-- TU CÓDIGO AQUÍ:



-- PASO 3: Inserta 4 clientes:
--   | nombre         | email                  | ciudad     |
--   |----------------|------------------------|------------|
--   | Laura Méndez   | laura@email.com        | CDMX       |
--   | Roberto Silva  | roberto@email.com      | Monterrey  |
--   | Sofia Torres   | sofia@email.com        | Guadalajara|
--   | Diego Ramírez  | diego@email.com        | CDMX       |
-- TU CÓDIGO AQUÍ:



-- PASO 4: Inserta 6 pedidos:
--   | cliente_id | producto       | cantidad | total    | fecha       |
--   |------------|----------------|----------|----------|-------------|
--   | 1          | Laptop HP      | 1        | 12500.00 | 2026-01-15  |
--   | 1          | Mouse Logitech | 2        | 901.00   | 2026-01-15  |
--   | 2          | Monitor Samsung| 1        | 5600.00  | 2026-02-01  |
--   | 3          | Teclado mecánico| 1       | 1200.00  | 2026-02-10  |
--   | 3          | Cable HDMI     | 3        | 450.00   | 2026-02-10  |
--   | 4          | Laptop HP      | 1        | 12500.00 | 2026-02-20  |
-- TU CÓDIGO AQUÍ:



-- PREGUNTA A: Muestra el nombre del cliente y qué productos pidió (usa JOIN)
-- TU CÓDIGO AQUÍ:



-- PREGUNTA B: ¿Cuánto gastó en total cada cliente? (usa SUM y GROUP BY)
-- TU CÓDIGO AQUÍ:



-- PREGUNTA C: Muestra solo los pedidos de clientes de "CDMX"
-- TU CÓDIGO AQUÍ:



-- PREGUNTA D: ¿Cuántos pedidos hizo cada cliente?
-- TU CÓDIGO AQUÍ:

