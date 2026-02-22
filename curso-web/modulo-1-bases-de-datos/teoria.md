# Módulo 1: Bases de Datos desde Cero

## 1.1 ¿Qué es una base de datos?

Imagina una **hoja de Excel** gigante donde guardas información organizada.
Eso es, en esencia, una base de datos: un lugar donde almacenas datos de
forma ordenada para poder encontrarlos y usarlos después.

### Ejemplo del mundo real

Piensa en la agenda de contactos de tu teléfono:
- Cada contacto tiene: **nombre**, **teléfono**, **email**
- Puedes **buscar** por nombre
- Puedes **agregar** nuevos contactos
- Puedes **borrar** o **editar** contactos existentes

Eso es exactamente lo que hace una base de datos, pero a gran escala.

---

## 1.2 ¿Qué es una tabla?

Una **tabla** es como una hoja dentro de Excel. Tiene:
- **Columnas** (campos): definen QUÉ tipo de dato guardas
- **Filas** (registros): son los datos en sí

### Ejemplo visual

Tabla: `contactos`

| id | nombre   | telefono   | email              |
|----|----------|------------|--------------------|
| 1  | María    | 5551234567 | maria@email.com    |
| 2  | Carlos   | 5559876543 | carlos@email.com   |
| 3  | Ana      | 5555555555 | ana@email.com      |

- `id`, `nombre`, `telefono`, `email` son las **columnas**
- Cada fila (María, Carlos, Ana) es un **registro**

---

## 1.3 ¿Qué es SQL?

**SQL** (Structured Query Language) es el idioma que usas para hablar con
la base de datos. Con SQL le dices a la base de datos qué quieres hacer.

Hay 4 operaciones fundamentales, conocidas como **CRUD**:

| Operación | SQL      | Significado            |
|-----------|----------|------------------------|
| **C**reate| INSERT   | Crear/agregar datos    |
| **R**ead  | SELECT   | Leer/consultar datos   |
| **U**pdate| UPDATE   | Actualizar/modificar   |
| **D**elete| DELETE   | Borrar datos           |

---

## 1.4 Crear una base de datos y una tabla

```sql
-- Paso 1: Crear la base de datos
CREATE DATABASE mi_primera_db;

-- Paso 2: Decirle a MySQL que la use
USE mi_primera_db;

-- Paso 3: Crear una tabla
CREATE TABLE contactos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    email VARCHAR(100)
);
```

### Explicación línea por línea:

- `CREATE DATABASE mi_primera_db;` → Crea una base de datos nueva
- `USE mi_primera_db;` → Le dice a MySQL: "quiero trabajar con esta"
- `CREATE TABLE contactos (...)` → Crea una tabla llamada "contactos"
- `id INT AUTO_INCREMENT PRIMARY KEY` → Un número único que se genera solo
- `nombre VARCHAR(100) NOT NULL` → Texto de hasta 100 caracteres, obligatorio
- `telefono VARCHAR(15)` → Texto de hasta 15 caracteres (opcional)
- `email VARCHAR(100)` → Texto de hasta 100 caracteres (opcional)

---

## 1.5 Insertar datos (CREATE)

```sql
-- Agregar un contacto
INSERT INTO contactos (nombre, telefono, email)
VALUES ('María López', '5551234567', 'maria@email.com');

-- Agregar varios contactos de una vez
INSERT INTO contactos (nombre, telefono, email) VALUES
    ('Carlos Ruiz', '5559876543', 'carlos@email.com'),
    ('Ana García', '5555555555', 'ana@email.com'),
    ('Pedro Martínez', '5551112233', 'pedro@email.com');
```

**Nota:** No ponemos el `id` porque se genera automáticamente gracias a `AUTO_INCREMENT`.

---

## 1.6 Leer datos (READ)

```sql
-- Traer TODOS los contactos
SELECT * FROM contactos;

-- Traer solo nombre y email
SELECT nombre, email FROM contactos;

-- Buscar un contacto específico
SELECT * FROM contactos WHERE nombre = 'María López';

-- Buscar contactos cuyo nombre empiece con 'A'
SELECT * FROM contactos WHERE nombre LIKE 'A%';

-- Ordenar por nombre (A a Z)
SELECT * FROM contactos ORDER BY nombre ASC;

-- Contar cuántos contactos tengo
SELECT COUNT(*) FROM contactos;
```

### Desglose de SELECT:

- `SELECT *` → Selecciona todas las columnas
- `FROM contactos` → De la tabla "contactos"
- `WHERE ...` → Condición/filtro
- `LIKE 'A%'` → Que empiece con 'A' (% = cualquier cosa después)
- `ORDER BY nombre ASC` → Ordenar por nombre, ascendente
- `COUNT(*)` → Cuenta las filas

---

## 1.7 Actualizar datos (UPDATE)

```sql
-- Cambiar el teléfono de María
UPDATE contactos
SET telefono = '5550001111'
WHERE nombre = 'María López';

-- Cambiar email y teléfono de Carlos
UPDATE contactos
SET email = 'carlos.nuevo@email.com', telefono = '5552223344'
WHERE id = 2;
```

**MUY IMPORTANTE:** Siempre usa `WHERE` en un UPDATE.
Sin WHERE, actualizarías TODOS los registros. Eso sería un desastre.

---

## 1.8 Borrar datos (DELETE)

```sql
-- Borrar un contacto específico
DELETE FROM contactos WHERE id = 4;

-- Borrar todos los contactos (¡CUIDADO!)
DELETE FROM contactos;
```

**MUY IMPORTANTE:** Igual que con UPDATE, siempre usa `WHERE` en DELETE.
Sin WHERE, borras TODO.

---

## 1.9 Tipos de datos más comunes

| Tipo          | Para qué sirve                    | Ejemplo          |
|---------------|-----------------------------------|------------------|
| INT           | Números enteros                   | 1, 42, 1000      |
| VARCHAR(n)    | Texto corto (hasta n caracteres)  | 'Hola mundo'     |
| TEXT          | Texto largo                       | Un artículo      |
| DECIMAL(m,d)  | Números con decimales             | 99.99            |
| DATE          | Fechas                            | '2026-02-22'     |
| DATETIME      | Fecha y hora                      | '2026-02-22 10:30:00' |
| BOOLEAN       | Verdadero/Falso                   | TRUE, FALSE      |

---

## 1.10 Concepto clave: PRIMARY KEY

La **PRIMARY KEY** (clave primaria) es un valor ÚNICO que identifica cada registro.
Es como el número de credencial de un estudiante: no se repite.

Normalmente se usa un campo `id` de tipo `INT AUTO_INCREMENT`.

---

## 1.11 Concepto clave: Relaciones entre tablas

Las tablas pueden estar **relacionadas** entre sí. Ejemplo:

Tabla `clientes`:
| id | nombre    |
|----|-----------|
| 1  | María     |
| 2  | Carlos    |

Tabla `pedidos`:
| id | cliente_id | producto  | total  |
|----|------------|-----------|--------|
| 1  | 1          | Laptop    | 15000  |
| 2  | 1          | Mouse     | 350    |
| 3  | 2          | Teclado   | 800    |

El campo `cliente_id` en la tabla `pedidos` **apunta** al `id` de la tabla `clientes`.
Esto se llama **FOREIGN KEY** (clave foránea).

```sql
-- Crear tabla pedidos con relación a clientes
CREATE TABLE pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    producto VARCHAR(100) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

-- Consultar pedidos con nombre del cliente (JOIN)
SELECT clientes.nombre, pedidos.producto, pedidos.total
FROM pedidos
JOIN clientes ON pedidos.cliente_id = clientes.id;
```

---

## Resumen del Módulo 1

| Concepto        | Qué es                                        |
|-----------------|------------------------------------------------|
| Base de datos   | Almacén organizado de información              |
| Tabla           | Estructura con columnas y filas                |
| SQL             | Lenguaje para hablar con la base de datos      |
| CRUD            | Create, Read, Update, Delete                   |
| PRIMARY KEY     | Identificador único de cada registro           |
| FOREIGN KEY     | Conexión entre dos tablas                      |
| JOIN            | Consulta que une datos de varias tablas        |

---

**Siguiente paso:** Ve a la carpeta `ejercicios/` y resuelve los retos.
