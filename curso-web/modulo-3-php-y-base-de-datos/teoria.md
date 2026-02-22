# Módulo 3: Conectar PHP con Base de Datos

## 3.1 ¿Cómo se comunica PHP con MySQL?

Hasta ahora aprendiste SQL (para hablar con la base de datos) y PHP (para
programar lógica). Ahora vamos a **conectarlos**: PHP le enviará comandos SQL
a MySQL y recibirá los resultados.

```
[Usuario] → [PHP] → [MySQL] → [Datos]
   ↑                              |
   └──────────────────────────────┘
```

PHP usa una herramienta llamada **PDO** (PHP Data Objects) para conectarse
a la base de datos de forma segura.

---

## 3.2 Conectarse a la base de datos

```php
<?php
$host = "localhost";       // Dirección del servidor de BD
$dbname = "tienda";        // Nombre de la base de datos
$usuario = "root";         // Usuario de MySQL
$password = "";            // Contraseña de MySQL

try {
    $conexion = new PDO(
        "mysql:host=$host;dbname=$dbname;charset=utf8",
        $usuario,
        $password
    );

    // Configurar PDO para que muestre errores
    $conexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    echo "Conexión exitosa a la base de datos\n";

} catch (PDOException $e) {
    echo "Error de conexión: " . $e->getMessage() . "\n";
    exit;
}
?>
```

### Explicación:
- `new PDO(...)` → Crea la conexión
- `"mysql:host=...;dbname=..."` → Le dice a PHP dónde está la base de datos
- `try/catch` → Si algo sale mal, captura el error en vez de romper todo
- `setAttribute(...)` → Configura PDO para mostrar errores claros

---

## 3.3 Operación CREATE (Insertar datos)

```php
<?php
// Método SEGURO: Prepared Statements (sentencias preparadas)
$sql = "INSERT INTO productos (nombre, precio, cantidad_en_stock, categoria)
        VALUES (:nombre, :precio, :stock, :categoria)";

$stmt = $conexion->prepare($sql);

$stmt->execute([
    ':nombre'    => 'Audífonos Sony',
    ':precio'    => 890.00,
    ':stock'     => 20,
    ':categoria' => 'Electrónica'
]);

echo "Producto insertado correctamente\n";
echo "ID del nuevo producto: " . $conexion->lastInsertId() . "\n";
?>
```

### ¿Qué son los Prepared Statements?

Son la forma SEGURA de enviar datos a la base de datos. Los `:nombre`,
`:precio`, etc. son **placeholders** (marcadores). PDO se encarga de
proteger los datos contra **inyección SQL** (un tipo de ataque).

**NUNCA hagas esto:**
```php
// PELIGROSO - vulnerable a inyección SQL
$sql = "INSERT INTO productos (nombre) VALUES ('$nombre')";
```

**SIEMPRE usa prepared statements:**
```php
// SEGURO
$sql = "INSERT INTO productos (nombre) VALUES (:nombre)";
$stmt = $conexion->prepare($sql);
$stmt->execute([':nombre' => $nombre]);
```

---

## 3.4 Operación READ (Leer datos)

### Obtener todos los registros
```php
<?php
$sql = "SELECT * FROM productos";
$stmt = $conexion->prepare($sql);
$stmt->execute();

$productos = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($productos as $producto) {
    echo $producto['nombre'] . " - $" . $producto['precio'] . "\n";
}
?>
```

### Obtener un registro específico
```php
<?php
$sql = "SELECT * FROM productos WHERE id = :id";
$stmt = $conexion->prepare($sql);
$stmt->execute([':id' => 1]);

$producto = $stmt->fetch(PDO::FETCH_ASSOC);

if ($producto) {
    echo "Producto: " . $producto['nombre'] . "\n";
    echo "Precio: $" . $producto['precio'] . "\n";
} else {
    echo "Producto no encontrado\n";
}
?>
```

### Buscar con filtros
```php
<?php
$sql = "SELECT * FROM productos WHERE precio > :precio_minimo ORDER BY precio ASC";
$stmt = $conexion->prepare($sql);
$stmt->execute([':precio_minimo' => 1000]);

$productos = $stmt->fetchAll(PDO::FETCH_ASSOC);
echo "Productos con precio mayor a $1000:\n";
foreach ($productos as $p) {
    echo "  - {$p['nombre']}: \${$p['precio']}\n";
}
?>
```

### Diferencia entre fetch() y fetchAll()
- `fetch()` → Trae **un solo** registro (el primero que encuentre)
- `fetchAll()` → Trae **todos** los registros que coincidan
- `PDO::FETCH_ASSOC` → Devuelve el resultado como array asociativo

---

## 3.5 Operación UPDATE (Actualizar datos)

```php
<?php
$sql = "UPDATE productos SET precio = :precio, cantidad_en_stock = :stock
        WHERE id = :id";

$stmt = $conexion->prepare($sql);
$stmt->execute([
    ':precio' => 950.00,
    ':stock'  => 18,
    ':id'     => 1
]);

$filas_afectadas = $stmt->rowCount();
echo "$filas_afectadas producto(s) actualizado(s)\n";
?>
```

- `rowCount()` → Te dice cuántas filas fueron afectadas por la operación

---

## 3.6 Operación DELETE (Borrar datos)

```php
<?php
$sql = "DELETE FROM productos WHERE id = :id";
$stmt = $conexion->prepare($sql);
$stmt->execute([':id' => 5]);

$filas_afectadas = $stmt->rowCount();

if ($filas_afectadas > 0) {
    echo "Producto eliminado correctamente\n";
} else {
    echo "No se encontró el producto con ese ID\n";
}
?>
```

---

## 3.7 Patrón completo: Archivo de conexión reutilizable

En un proyecto real, NO repites el código de conexión en cada archivo.
Lo pones en un solo archivo y lo incluyes donde lo necesites.

### Archivo: `conexion.php`
```php
<?php
function obtener_conexion() {
    $host = "localhost";
    $dbname = "tienda";
    $usuario = "root";
    $password = "";

    try {
        $conexion = new PDO(
            "mysql:host=$host;dbname=$dbname;charset=utf8",
            $usuario,
            $password
        );
        $conexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        return $conexion;
    } catch (PDOException $e) {
        die("Error de conexión: " . $e->getMessage());
    }
}
?>
```

### Archivo: `listar_productos.php`
```php
<?php
require_once 'conexion.php';

$conexion = obtener_conexion();

$stmt = $conexion->prepare("SELECT * FROM productos");
$stmt->execute();
$productos = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($productos as $p) {
    echo "{$p['id']}. {$p['nombre']} - \${$p['precio']}\n";
}
?>
```

El `require_once` incluye el archivo `conexion.php` una sola vez.

---

## 3.8 Manejo de errores

```php
<?php
try {
    $conexion = obtener_conexion();

    $stmt = $conexion->prepare("INSERT INTO productos (nombre, precio) VALUES (:nombre, :precio)");
    $stmt->execute([':nombre' => 'Test', ':precio' => 100]);

    echo "Operación exitosa\n";

} catch (PDOException $e) {
    echo "Error en la base de datos: " . $e->getMessage() . "\n";
}
?>
```

Siempre envuelve operaciones de BD en `try/catch` para capturar errores.

---

## 3.9 Contar registros

```php
<?php
$sql = "SELECT COUNT(*) as total FROM productos";
$stmt = $conexion->prepare($sql);
$stmt->execute();
$resultado = $stmt->fetch(PDO::FETCH_ASSOC);

echo "Total de productos: " . $resultado['total'] . "\n";
?>
```

---

## Resumen del Módulo 3

| Concepto             | Qué es                                          |
|----------------------|--------------------------------------------------|
| PDO                  | Herramienta de PHP para conectarse a BD          |
| Prepared Statements  | Forma segura de enviar datos a la BD             |
| fetch()              | Obtener un registro                              |
| fetchAll()           | Obtener todos los registros                      |
| execute()            | Ejecutar la consulta                             |
| lastInsertId()       | ID del último registro insertado                 |
| rowCount()           | Cantidad de filas afectadas                      |
| try/catch            | Manejo de errores                                |
| require_once         | Incluir un archivo una sola vez                  |

---

**Siguiente paso:** Ve a `ejercicios/` para practicar el CRUD completo.
