-- SOLUCIÓN Ejercicio 1.1: Crear tabla productos

CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(200),
    precio DECIMAL(10, 2),
    cantidad INT
);

-- ¿Coincidió con tu respuesta? ¡Perfecto!
-- Si usaste FLOAT en vez de DECIMAL, también está bien para empezar.
