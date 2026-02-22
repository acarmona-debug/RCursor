## 01. PHP básico (lo suficiente para APIs)

### Qué es PHP (para este curso)

PHP es un lenguaje que puede ejecutarse en un servidor para:

- leer una petición HTTP
- procesar lógica (validar, transformar)
- hablar con una base de datos
- devolver una respuesta (normalmente JSON)

### Ejecutar PHP

Un script:

```bash
php archivo.php
```

Una línea rápida:

```bash
php -r 'echo "hola\n";'
```

### Variables y tipos (sin complicarte)

- `$x = 1;` (número)
- `$s = "texto";` (string)
- `$b = true;` (boolean)
- `$a = [1, 2, 3];` (array “lista”)
- `$o = ["id" => 1, "title" => "Aprender"];` (array “mapa” / asociativo)

Imprimir para depurar:

```php
var_dump($o);
```

### Condiciones

```php
if ($b) {
  // ...
} else {
  // ...
}
```

### Funciones (bloques reutilizables)

```php
function saludar(string $nombre): string {
  return "Hola, " . $nombre;
}
```

### Manejo de errores (try/catch)

Cuando algo puede fallar (DB, JSON, etc.) usamos excepciones:

```php
try {
  // algo riesgoso
} catch (Throwable $e) {
  // responder error
}
```

### JSON (clave para APIs)

En APIs vamos a:

- **recibir JSON** (body de la request)
- **devolver JSON** (body de la response)

Convertir JSON a array (decode):

```php
$data = json_decode($json, true);
```

Convertir array a JSON (encode):

```php
$json = json_encode($data);
```

**Regla importante**: si `json_decode` devuelve `null`, puede ser JSON inválido *o* el JSON literal `null`. Por eso en el proyecto validamos el error.

### Mini-checkpoint (2 minutos)

Ejecuta el sandbox (lo vas a tener en `proyecto/sandbox/php_basics.php`):

```bash
cd curso-php-api/proyecto
php sandbox/php_basics.php
```

**Checkpoint**: deberías ver en consola ejemplos de arrays, funciones y JSON.

