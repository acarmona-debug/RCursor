## Proyecto: API de “todos” (PHP + SQLite)

Este proyecto es una API pequeña (JSON) para practicar:

- endpoints (HTTP)
- CRUD
- SQLite con PDO

### Ejecutar

```bash
cd curso-php-api/proyecto
php -S localhost:8000 -t public
```

### Endpoints

- `GET /health`
- `GET /todos`
- `POST /todos`
- `GET /todos/{id}`
- `PATCH /todos/{id}`
- `DELETE /todos/{id}`

### Probar rápido con `curl`

Health:

```bash
curl -i http://localhost:8000/health
```

Crear:

```bash
curl -i -X POST http://localhost:8000/todos \
  -H 'Content-Type: application/json' \
  -d '{"title":"Aprender PHP","done":false}'
```

Listar:

```bash
curl -i http://localhost:8000/todos
```

Actualizar:

```bash
curl -i -X PATCH http://localhost:8000/todos/1 \
  -H 'Content-Type: application/json' \
  -d '{"done":true}'
```

Borrar:

```bash
curl -i -X DELETE http://localhost:8000/todos/1
```

### Base de datos

- Archivo: `storage/database.sqlite`
- Se crea automáticamente al primer uso.

