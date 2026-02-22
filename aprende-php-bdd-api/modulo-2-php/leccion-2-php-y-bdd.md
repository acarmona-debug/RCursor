# Lección 2: PHP conectado a una Base de Datos

## PDO - La forma segura de conectar

**PDO** (PHP Data Objects) nos permite conectar PHP con MySQL, SQLite, PostgreSQL, etc.

### Conexión con SQLite (más fácil para practicar)

```php
<?php
try {
    $pdo = new PDO('sqlite:mi_base_datos.db');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    echo "¡Conectado!\n";
} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>
```

### Conexión con MySQL

```php
<?php
$host = 'localhost';
$db   = 'mi_base_datos';
$user = 'usuario';
$pass = 'contraseña';

$pdo = new PDO("mysql:host=$host;dbname=$db", $user, $pass);
?>
```

---

## Hacer consultas

### SELECT (leer)
```php
<?php
$stmt = $pdo->query("SELECT * FROM usuarios");
$usuarios = $stmt->fetchAll(PDO::FETCH_ASSOC);

foreach ($usuarios as $usuario) {
    echo $usuario['nombre'] . " - " . $usuario['email'] . "\n";
}
?>
```

### INSERT (insertar) - Forma segura con parámetros
```php
<?php
$sql = "INSERT INTO usuarios (nombre, email) VALUES (:nombre, :email)";
$stmt = $pdo->prepare($sql);
$stmt->execute([
    'nombre' => 'Nuevo Usuario',
    'email' => 'nuevo@email.com'
]);
echo "Usuario insertado. ID: " . $pdo->lastInsertId();
?>
```

⚠️ **Importante:** Usa siempre `prepare()` y `execute()` con parámetros. Evita poner valores directamente en la consulta (previene inyección SQL).

---

## ✏️ Ejercicio 2.2

Modifica el archivo `conectar-y-listar.php` para que:
1. Se conecte a la base de datos SQLite `datos.db`
2. Cree la tabla `usuarios` si no existe
3. Inserte un usuario de prueba
4. Liste todos los usuarios
