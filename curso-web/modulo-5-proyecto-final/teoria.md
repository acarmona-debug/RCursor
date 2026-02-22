# Módulo 5: Proyecto Final - Sistema de Gestión de Tareas (To-Do API)

## Objetivo

Vas a construir un **sistema completo** que integra todo lo que aprendiste:

1. **Base de datos** → Diseño de tablas con relaciones
2. **PHP** → Lógica del servidor, funciones, validación
3. **API REST** → Endpoints para CRUD completo
4. **Llamadas HTTP** → Script cliente que consume la API

---

## El Proyecto: API de lista de tareas (To-Do)

### Funcionalidades:
- Gestionar **usuarios** (crear, listar)
- Gestionar **tareas** por usuario (crear, leer, actualizar estado, eliminar)
- Filtrar tareas por estado (pendiente, en progreso, completada)
- Estadísticas: cuántas tareas tiene cada usuario

### Estructura de la base de datos:

```
Tabla: usuarios
├── id (INT, AUTO_INCREMENT, PK)
├── nombre (VARCHAR 100, NOT NULL)
├── email (VARCHAR 100, NOT NULL, UNIQUE)
└── created_at (TIMESTAMP)

Tabla: tareas
├── id (INT, AUTO_INCREMENT, PK)
├── usuario_id (INT, FK → usuarios.id)
├── titulo (VARCHAR 200, NOT NULL)
├── descripcion (TEXT)
├── estado (ENUM: 'pendiente', 'en_progreso', 'completada')
├── prioridad (ENUM: 'baja', 'media', 'alta')
├── fecha_limite (DATE)
└── created_at (TIMESTAMP)
```

### Endpoints que debes crear:

| Método | Endpoint                        | Acción                          |
|--------|--------------------------------|---------------------------------|
| GET    | /api/usuarios                   | Listar todos los usuarios       |
| POST   | /api/usuarios                   | Crear un usuario                |
| GET    | /api/tareas?usuario_id=1        | Tareas de un usuario            |
| GET    | /api/tareas?estado=pendiente    | Filtrar por estado              |
| POST   | /api/tareas                     | Crear una tarea                 |
| PUT    | /api/tareas?id=1                | Actualizar una tarea            |
| DELETE | /api/tareas?id=1                | Eliminar una tarea              |
| GET    | /api/estadisticas               | Resumen de tareas por usuario   |

---

## Pasos sugeridos

### Paso 1: Crear la base de datos
Ejecuta el archivo `setup.sql`.

### Paso 2: Crear el archivo de conexión
Usa `config/database.php`.

### Paso 3: Crear el endpoint de usuarios
Archivo: `api/usuarios.php`

### Paso 4: Crear el endpoint de tareas
Archivo: `api/tareas.php`

### Paso 5: Crear el endpoint de estadísticas
Archivo: `api/estadisticas.php`

### Paso 6: Crear un script cliente
Archivo: `cliente.php` → que consuma tu propia API

### Paso 7: Probar todo
Usa cURL o el script de pruebas proporcionado.

---

## Criterios de éxito

Tu proyecto está completo cuando puedas:
- [ ] Crear un usuario por API
- [ ] Crear tareas asignadas a un usuario
- [ ] Listar tareas filtrando por usuario o estado
- [ ] Actualizar el estado de una tarea
- [ ] Eliminar una tarea
- [ ] Ver estadísticas de tareas por usuario
- [ ] Manejar errores (404, 400) correctamente

---

**Los archivos base están en las carpetas `ejemplos/` y `ejercicios/`.**
**¡Buena suerte!**
