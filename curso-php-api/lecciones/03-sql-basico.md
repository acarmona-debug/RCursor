## 03. SQL básico (para que la DB deje de dar miedo)

### Qué es una tabla

Una tabla es como una hoja de cálculo:

- **columnas**: los campos (ej. `id`, `title`, `done`)
- **filas**: cada registro (cada “todo”)

En este curso usaremos una tabla `todos`.

### CRUD en SQL

- **Create**: `INSERT`
- **Read**: `SELECT`
- **Update**: `UPDATE`
- **Delete**: `DELETE`

### SELECT (leer)

```sql
SELECT id, title, done
FROM todos
ORDER BY id DESC
LIMIT 10;
```

Filtrar con WHERE:

```sql
SELECT id, title, done
FROM todos
WHERE done = 0
ORDER BY id DESC;
```

### INSERT (crear)

```sql
INSERT INTO todos (title, done)
VALUES ('Aprender SQL', 0);
```

### UPDATE (actualizar)

```sql
UPDATE todos
SET done = 1
WHERE id = 1;
```

### DELETE (borrar)

```sql
DELETE FROM todos
WHERE id = 1;
```

### Clave de seguridad: parámetros (evitar SQL injection)

Cuando el valor viene del usuario (ej. `title`), NO lo concatenes en el SQL.

En PHP usaremos **prepared statements**:

- SQL con `?` o `:name`
- valores aparte (PDO hace el escape correctamente)

### Mini-checkpoint (3 minutos)

En `lecciones/04-php-pdo-sqlite.md` vas a ver cómo estos SQL se ejecutan desde PHP.

