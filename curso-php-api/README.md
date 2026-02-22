## Curso interactivo: Bases de datos + PHP + endpoints (desde cero)

Este material está pensado para que aprendas **desde cero** y de forma práctica:

- **PHP** (lo mínimo para empezar a programar)
- **HTTP** (llamadas básicas) y **endpoints** (APIs)
- **Bases de datos** con **SQLite** (SQL + conexión desde PHP con PDO)

La idea es que avances en este orden:

1. Lee una lección corta (10–20 min)
2. Haz ejercicios clave (con “checkpoint” para verificar)
3. Ejecuta el proyecto y prueba endpoints con `curl`

### Requisitos

- **PHP 8.x** instalado (en Linux suele ser `php`)
- Extensión **SQLite** habilitada (PDO SQLite)

Comprueba que PHP existe:

```bash
php -v
```

### Contenido

- Lecciones (lee en orden):
  - `lecciones/00-como-trabajaremos.md`
  - `lecciones/01-php-basico.md`
  - `lecciones/02-http-endpoints.md`
  - `lecciones/03-sql-basico.md`
  - `lecciones/04-php-pdo-sqlite.md`
- Ejercicios (haz en orden):
  - `ejercicios/01-php.md`
  - `ejercicios/02-http.md`
  - `ejercicios/03-sql.md`
  - `ejercicios/04-api-crud.md`
- Proyecto ejecutable (API de “todos”):
  - `proyecto/` (instrucciones en `proyecto/README.md`)

### Cómo empezar (10 minutos)

1) Lee `lecciones/00-como-trabajaremos.md`.

2) Ejecuta el proyecto:

```bash
cd curso-php-api/proyecto
php -S localhost:8000 -t public
```

3) En otra terminal, prueba el primer endpoint:

```bash
curl -s http://localhost:8000/health
```

Deberías ver un JSON con `{"ok":true,...}`.

