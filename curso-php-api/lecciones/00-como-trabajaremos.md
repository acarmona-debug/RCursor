## 00. Cómo vamos a trabajar (modo instructor)

### Objetivo final del mini-curso

Vas a construir y entender una **API** (endpoints HTTP) en **PHP** que guarda datos en una **base de datos SQLite**. La vas a probar con **curl** (como si fueras un cliente).

### Qué significa cada cosa (en 2 líneas)

- **Endpoint**: una “ruta” en un servidor (ej. `GET /todos`) que devuelve algo (normalmente JSON).
- **Llamada HTTP**: una petición a un endpoint (GET/POST/PATCH/DELETE).
- **Base de datos**: donde guardas información de forma persistente.
- **SQL**: el lenguaje para crear/consultar/modificar datos en la base.

### Tu rutina (muy concreta)

Para cada módulo:

1) Lee la lección (10–20 min)  
2) Haz los ejercicios del archivo correspondiente  
3) Verifica con los “checkpoints” (salida esperada)  

### Herramientas que vas a usar

- `php`: ejecutar scripts y levantar el servidor
- `curl`: hacer llamadas a endpoints (como un cliente)
- SQLite (a través de PHP con PDO): base de datos local en un archivo

### Regla de oro

Cuando algo no funciona, lo depuramos en este orden:

1) ¿Qué endpoint llamé? (URL + método)
2) ¿Qué mandé? (headers + body JSON)
3) ¿Qué devolvió? (status code + JSON de error)
4) ¿Qué dice el servidor? (mensajes de PHP)
5) ¿Qué hay en la base de datos? (¿insertó? ¿consultó?)

### Micro-ejercicio de arranque (sin programar)

1) Arranca el servidor del proyecto:

```bash
cd curso-php-api/proyecto
php -S localhost:8000 -t public
```

2) Desde otra terminal, llama al “health check”:

```bash
curl -i http://localhost:8000/health
```

**Checkpoint**:

- Deberías ver `HTTP/1.1 200 OK`
- Deberías ver `Content-Type: application/json`
- Y un JSON con `ok: true`

