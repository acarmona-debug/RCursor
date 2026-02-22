## Ejercicio 01 — PHP básico (sin web todavía)

### Objetivo

Sentirte cómodo con:

- variables, arrays y funciones
- JSON encode/decode
- manejar errores básicos

### Preparación

```bash
cd curso-php-api/proyecto
php sandbox/php_basics.php
```

### Tareas

1) **Lee la salida** del script y ubica:
   - un array “lista”
   - un array “mapa” (asociativo)
   - un JSON generado con `json_encode`

2) Abre `proyecto/sandbox/php_basics.php` y modifica:
   - el `title` del ejemplo
   - agrega un campo nuevo, por ejemplo `"level" => "beginner"`

3) Vuelve a ejecutar el script.

### Checkpoint

Deberías ver el JSON actualizado con tu nuevo campo.

### Extra (si vas rápido)

Agrega una función `isValidTitle($title)` que devuelva `true/false` y pruébala con 3 casos:

- `"Hola"` (true)
- `""` (false)
- `"   "` (false)

