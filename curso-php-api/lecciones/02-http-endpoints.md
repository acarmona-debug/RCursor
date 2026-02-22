## 02. HTTP + endpoints (APIs desde cero)

### El modelo mental

Un cliente (tu navegador o `curl`) hace una **request** a un servidor.

La request tiene:

- **método**: GET / POST / PATCH / DELETE
- **URL**: `http://localhost:8000/todos?limit=10`
- **headers**: metadatos (ej. `Content-Type: application/json`)
- **body**: datos (normalmente JSON en POST/PATCH)

El servidor responde con:

- **status code**: 200, 201, 400, 404, 500...
- **headers**
- **body** (JSON)

### Métodos HTTP (cómo los usamos aquí)

- **GET**: leer datos (no debería modificar nada)
- **POST**: crear algo nuevo
- **PATCH**: actualizar parcialmente
- **DELETE**: borrar

### Status codes (los más útiles)

- **200 OK**: todo bien (GET/DELETE con respuesta)
- **201 Created**: se creó algo (POST)
- **400 Bad Request**: el cliente mandó algo inválido (JSON mal, datos faltantes)
- **404 Not Found**: endpoint o recurso no existe
- **422 Unprocessable Content**: el formato es válido, pero falla validación (ej. `title` vacío)
- **500 Internal Server Error**: bug o fallo inesperado del servidor

### “Endpoint” vs “recurso”

Si hablamos de “todos”:

- Endpoint para listar: `GET /todos`
- Endpoint para crear: `POST /todos`
- Recurso específico: `GET /todos/123`

### `curl` (tu herramienta de práctica)

Ver headers y status:

```bash
curl -i http://localhost:8000/health
```

Enviar JSON (POST):

```bash
curl -i -X POST http://localhost:8000/todos \
  -H 'Content-Type: application/json' \
  -d '{"title":"Aprender endpoints","done":false}'
```

### Mini-checkpoint (5 minutos)

1) Llama un endpoint que existe:

```bash
curl -i http://localhost:8000/health
```

2) Llama un endpoint que NO existe:

```bash
curl -i http://localhost:8000/no-existe
```

**Checkpoint**:

- el primero debe dar **200**
- el segundo debe dar **404** con JSON de error

