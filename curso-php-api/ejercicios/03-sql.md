## Ejercicio 03 — SQL (pensar en tablas y consultas)

### Objetivo

Entender cómo tus endpoints se traducen a operaciones SQL:

- `POST /todos` → `INSERT`
- `GET /todos` → `SELECT`
- `PATCH /todos/:id` → `UPDATE`
- `DELETE /todos/:id` → `DELETE`

### Preparación

1) Levanta el servidor:

```bash
cd curso-php-api/proyecto
php -S localhost:8000 -t public
```

2) Crea 3 todos:

```bash
curl -s -X POST http://localhost:8000/todos -H 'Content-Type: application/json' -d '{"title":"A","done":false}'
curl -s -X POST http://localhost:8000/todos -H 'Content-Type: application/json' -d '{"title":"B","done":true}'
curl -s -X POST http://localhost:8000/todos -H 'Content-Type: application/json' -d '{"title":"C","done":false}'
```

### Tareas

1) Lista todos y fíjate en `id`:

```bash
curl -s http://localhost:8000/todos
```

2) Toma un `id` y tráelo por endpoint:

```bash
curl -s http://localhost:8000/todos/1
```

3) Marca un todo como hecho:

```bash
curl -s -X PATCH http://localhost:8000/todos/1 \
  -H 'Content-Type: application/json' \
  -d '{"done":true}'
```

4) Vuelve a listar y verifica que cambió.

### Checkpoint

Puedes explicar (en voz alta o en una nota) qué SQL crees que ocurre detrás de cada endpoint.

### Extra (opcional)

Si tienes `sqlite3` instalado, inspecciona la DB:

```bash
sqlite3 curso-php-api/proyecto/storage/database.sqlite "select id,title,done from todos order by id desc limit 10;"
```

