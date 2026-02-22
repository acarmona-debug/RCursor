# Guía Rápida de Referencia

## SQL - Comandos esenciales

```sql
-- Crear base de datos
CREATE DATABASE nombre_db;
USE nombre_db;

-- Crear tabla
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE
);

-- CRUD
INSERT INTO usuarios (nombre, email) VALUES ('Ana', 'ana@email.com');
SELECT * FROM usuarios WHERE nombre LIKE '%ana%';
UPDATE usuarios SET email = 'nuevo@email.com' WHERE id = 1;
DELETE FROM usuarios WHERE id = 1;

-- Funciones útiles
SELECT COUNT(*) FROM usuarios;
SELECT AVG(precio) FROM productos;
SELECT * FROM productos ORDER BY precio DESC LIMIT 10;

-- JOIN
SELECT u.nombre, p.producto
FROM pedidos p
JOIN usuarios u ON p.usuario_id = u.id;
```

---

## PHP - Lo esencial

```php
<?php
// Variables
$nombre = "Carlos";
$edad = 25;
$precios = [100, 200, 300];
$persona = ["nombre" => "Ana", "edad" => 30];

// Condicional
if ($edad >= 18) { echo "Mayor"; } else { echo "Menor"; }

// Ciclos
for ($i = 0; $i < 10; $i++) { echo $i; }
foreach ($precios as $precio) { echo $precio; }

// Funciones
function sumar($a, $b) { return $a + $b; }

// Strings
strlen($texto);
strtoupper($texto);
str_contains($texto, "buscar");
```

---

## PHP + MySQL (PDO)

```php
<?php
// Conexión
$db = new PDO("mysql:host=localhost;dbname=midb;charset=utf8", "root", "");
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// INSERT
$stmt = $db->prepare("INSERT INTO users (name) VALUES (:name)");
$stmt->execute([':name' => 'Ana']);
$id = $db->lastInsertId();

// SELECT todos
$stmt = $db->prepare("SELECT * FROM users");
$stmt->execute();
$users = $stmt->fetchAll(PDO::FETCH_ASSOC);

// SELECT uno
$stmt = $db->prepare("SELECT * FROM users WHERE id = :id");
$stmt->execute([':id' => 1]);
$user = $stmt->fetch(PDO::FETCH_ASSOC);

// UPDATE
$stmt = $db->prepare("UPDATE users SET name = :name WHERE id = :id");
$stmt->execute([':name' => 'Ana García', ':id' => 1]);

// DELETE
$stmt = $db->prepare("DELETE FROM users WHERE id = :id");
$stmt->execute([':id' => 1]);
```

---

## API en PHP

```php
<?php
header('Content-Type: application/json');

$metodo = $_SERVER['REQUEST_METHOD'];  // GET, POST, PUT, DELETE
$id = $_GET['id'] ?? null;            // Parámetro de URL

// Leer body JSON
$datos = json_decode(file_get_contents('php://input'), true);

// Responder JSON
http_response_code(200);
echo json_encode(["status" => "success", "data" => $resultado]);
```

---

## cURL - Probar APIs

```bash
# GET
curl http://localhost:8000/api/productos.php

# POST
curl -X POST http://localhost:8000/api/productos.php \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test","precio":100}'

# PUT
curl -X PUT "http://localhost:8000/api/productos.php?id=1" \
  -H "Content-Type: application/json" \
  -d '{"precio":200}'

# DELETE
curl -X DELETE "http://localhost:8000/api/productos.php?id=1"

# Iniciar servidor PHP
php -S localhost:8000
```

---

## Códigos HTTP

| Código | Significado              |
|--------|--------------------------|
| 200    | OK                       |
| 201    | Creado                   |
| 400    | Error en la petición     |
| 404    | No encontrado            |
| 405    | Método no permitido      |
| 500    | Error del servidor       |
