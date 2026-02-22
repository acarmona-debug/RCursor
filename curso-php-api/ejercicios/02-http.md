## Ejercicio 02 — HTTP y endpoints (con `curl`)

### Objetivo

Aprender a leer:

- método + URL
- status code
- headers (especialmente `Content-Type`)
- JSON de respuesta

### Preparación

En una terminal:

```bash
cd curso-php-api/proyecto
php -S localhost:8000 -t public
```

### Tareas

1) **Health check**

```bash
curl -i http://localhost:8000/health
```

2) **Listar todos**

```bash
curl -i http://localhost:8000/todos
```

3) **Crear un todo (POST con JSON)**

```bash
curl -i -X POST http://localhost:8000/todos \
  -H 'Content-Type: application/json' \
  -d '{"title":"Mi primer todo","done":false}'
```

4) **Error por JSON inválido**

```bash
curl -i -X POST http://localhost:8000/todos \
  -H 'Content-Type: application/json' \
  -d '{'
```

5) **Error por validación (title vacío)**

```bash
curl -i -X POST http://localhost:8000/todos \
  -H 'Content-Type: application/json' \
  -d '{"title":"   "}'
```

### Checkpoints

- `GET /health` debe dar **200**
- `GET /todos` debe dar **200** y un JSON con array
- `POST /todos` con buen JSON debe dar **201** y devolverte el registro creado
- JSON inválido debe dar **400**
- title inválido debe dar **422**

### Extra (si vas rápido)

Usa `curl -s` (silent) y luego `| python -m json.tool` para “formatear” el JSON.

