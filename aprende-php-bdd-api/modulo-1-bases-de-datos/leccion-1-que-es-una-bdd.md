# Módulo 1: Bases de Datos

## Lección 1: ¿Qué es una base de datos?

Una **base de datos** es como una **libreta digital** donde guardas información organizada.

### Analogía rápida
- **Hoja Excel** → Cada tabla
- **Columnas** → Campos (nombre, email, edad)
- **Filas** → Registros (cada persona, producto, etc.)

### ¿Por qué usar una base de datos?
- Guardar usuarios, productos, pedidos
- Buscar información rápido
- Evitar duplicados
- Mantener todo organizado

---

## Lección 2: SQL - El idioma de las bases de datos

**SQL** = Structured Query Language (Lenguaje de Consulta Estructurado)

Con SQL le dices a la base de datos qué hacer:
- **CREAR** tablas
- **INSERTAR** datos
- **LEER** datos
- **ACTUALIZAR** datos
- **ELIMINAR** datos

---

## Lección 3: Comandos básicos

### CREAR una tabla

```sql
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100),
    email VARCHAR(150),
    edad INT
);
```

Explicación:
- `CREATE TABLE` → crear tabla
- `id` → número único por fila
- `VARCHAR(100)` → texto máximo 100 caracteres
- `INT` → número entero
- `PRIMARY KEY` → identifica cada fila

### INSERTAR datos

```sql
INSERT INTO usuarios (nombre, email, edad) 
VALUES ('María', 'maria@email.com', 25);

INSERT INTO usuarios (nombre, email, edad) 
VALUES ('Carlos', 'carlos@email.com', 30);
```

### LEER datos (SELECT)

```sql
-- Ver TODOS los usuarios
SELECT * FROM usuarios;

-- Ver solo nombre y email
SELECT nombre, email FROM usuarios;

-- Filtrar por condición
SELECT * FROM usuarios WHERE edad > 25;

-- Ordenar por nombre
SELECT * FROM usuarios ORDER BY nombre ASC;
```

### ACTUALIZAR datos

```sql
UPDATE usuarios 
SET edad = 26, email = 'maria.nueva@email.com'
WHERE nombre = 'María';
```

### ELIMINAR datos

```sql
DELETE FROM usuarios WHERE nombre = 'Carlos';
```

---

## ✏️ Ejercicio 1.1

Crea una tabla llamada `productos` con estos campos:
- id (número, clave primaria, auto-incremento)
- nombre (texto)
- precio (número decimal)
- cantidad (número entero)

**Solución en:** `ejercicio-1-1-solucion.sql`

---

## ✏️ Ejercicio 1.2

Inserta 3 productos en la tabla que creaste.

**Solución en:** `ejercicio-1-2-solucion.sql`
