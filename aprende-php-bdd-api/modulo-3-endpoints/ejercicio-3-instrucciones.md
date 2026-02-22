# ✏️ Ejercicio 3: Crear tu propio endpoint

## Objetivo
Crea un endpoint GET que devuelva los **productos** más caros que un precio dado.

## Requisitos
1. Crea el archivo `api-productos.php`
2. Debe aceptar un parámetro `precio_minimo` por GET
3. Si no se pasa, usa 0 por defecto
4. Conecta a una base SQLite con tabla `productos` (id, nombre, precio, cantidad)
5. Devuelve en JSON los productos con precio >= precio_minimo
6. Si no hay productos, devuelve array vacío con éxito 200

## Ejemplo de uso
```
GET /api-productos.php?precio_minimo=50
```

## Respuesta esperada
```json
{
  "success": true,
  "data": [
    {"id": 1, "nombre": "Laptop", "precio": 999.99, "cantidad": 10}
  ]
}
```

## Plantilla

```php
<?php
header('Content-Type: application/json; charset=utf-8');
// 1. Obtener precio_minimo de $_GET
// 2. Conectar a SQLite (crea productos.db si no existe)
// 3. Crear tabla productos si no existe, insertar datos de prueba
// 4. Consultar: SELECT * FROM productos WHERE precio >= ?
// 5. Devolver JSON
?>
```

## Solución
Ver `api-productos-solucion.php` cuando termines de intentarlo.
