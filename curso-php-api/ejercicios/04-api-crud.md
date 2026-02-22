## Ejercicio 04 — CRUD completo (extender la API)

### Objetivo

Extender una API real:

- validar mejor entradas
- agregar filtros por query params
- devolver errores consistentes

### Preparación

```bash
cd curso-php-api/proyecto
php -S localhost:8000 -t public
```

### Tarea A — Filtrar por `done` en `GET /todos`

Meta:

- `GET /todos` → devuelve todos
- `GET /todos?done=1` → devuelve sólo hechos
- `GET /todos?done=0` → devuelve sólo pendientes

Pista:

- mira `src/todos.php` (función de listado)
- lee query params desde la request (mira `src/request.php`)

Checkpoint:

```bash
curl -s "http://localhost:8000/todos?done=1"
curl -s "http://localhost:8000/todos?done=0"
```

### Tarea B — Validación más clara para `POST /todos`

Meta: si `title` viene vacío o no es string, devolver:

- status **422**
- JSON con un campo `errors` (array) explicando el problema

Pista: revisa cómo el proyecto arma respuestas de error en `src/response.php`.

### Tarea C (extra) — Endpoint `GET /stats`

Meta: devolver conteos:

```json
{ "total": 10, "done": 4, "pending": 6 }
```

SQL sugerido:

- `COUNT(*)` total
- `SUM(done)` hechos (si `done` es 0/1)

