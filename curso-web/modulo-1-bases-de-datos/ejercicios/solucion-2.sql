-- ============================================================
-- SOLUCIÓN EJERCICIO 2: Consultas SQL
-- ============================================================

-- PREGUNTA 1: Nombre y precio
SELECT nombre, precio FROM productos;

-- PREGUNTA 2: Productos de categoría "Accesorios"
SELECT * FROM productos WHERE categoria = 'Accesorios';

-- PREGUNTA 3: Productos con precio mayor a 1000
SELECT * FROM productos WHERE precio > 1000;

-- PREGUNTA 4: Ordenados del más caro al más barato
SELECT * FROM productos ORDER BY precio DESC;

-- PREGUNTA 5: Total de productos
SELECT COUNT(*) AS total_productos FROM productos;

-- PREGUNTA 6: Precio promedio
SELECT AVG(precio) AS precio_promedio FROM productos;

-- PREGUNTA 7: Más de 20 unidades en stock
SELECT * FROM productos WHERE cantidad_en_stock > 20;

-- PREGUNTA 8: Actualizar precio del Cable HDMI
UPDATE productos SET precio = 180.00 WHERE nombre = 'Cable HDMI';

-- PREGUNTA 9: Sumar 5 al stock del Monitor Samsung
UPDATE productos SET cantidad_en_stock = cantidad_en_stock + 5 WHERE nombre = 'Monitor Samsung';

-- PREGUNTA 10: Eliminar Cable HDMI
DELETE FROM productos WHERE nombre = 'Cable HDMI';
