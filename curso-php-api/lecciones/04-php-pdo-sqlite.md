## 04. PHP + SQLite con PDO (conexión real a una DB)

### Qué es PDO

PDO (PHP Data Objects) es la forma estándar en PHP de conectarse a bases de datos (SQLite, MySQL, Postgres, etc.).

En este curso:

- Base de datos: **SQLite**
- Se guarda en un archivo: `proyecto/storage/database.sqlite`

### Conectarte a SQLite (idea general)

```php
$db = new PDO("sqlite:/ruta/a/database.sqlite");
$db->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
```

### Prepared statements (lo correcto)

Ejemplo: insertar un todo sin SQL injection:

```php
$stmt = $db->prepare("INSERT INTO todos (title, done) VALUES (:title, :done)");
$stmt->execute([
  ":title" => $title,
  ":done" => $done ? 1 : 0,
]);
```

Consultar:

```php
$stmt = $db->query("SELECT id, title, done FROM todos ORDER BY id DESC");
$rows = $stmt->fetchAll(PDO::FETCH_ASSOC);
```

### Mini-checkpoint (5 minutos)

Ejecuta el sandbox de PDO:

```bash
cd curso-php-api/proyecto
php sandbox/pdo_sqlite.php
```

**Checkpoint**:

- se crea la base SQLite (si no existe)
- se crea la tabla (si no existe)
- se inserta al menos 1 fila
- se imprime un array con filas

### Siguiente paso

Ya con esto, estás listo para lo importante: **endpoints** que hagan CRUD contra la base.
Eso lo practicas en `ejercicios/04-api-crud.md` con el proyecto.

