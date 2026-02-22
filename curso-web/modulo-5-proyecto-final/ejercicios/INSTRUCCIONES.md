# Proyecto Final: Tu Turno

## Reto

Construye tu propio sistema de gestión desde cero. Elige UNO de estos proyectos:

### Opción A: Sistema de Biblioteca
- **Tabla `libros`**: id, titulo, autor, genero, disponible (boolean)
- **Tabla `prestamos`**: id, libro_id, nombre_persona, fecha_prestamo, fecha_devolucion
- **Endpoints**: CRUD de libros + registrar/devolver préstamos

### Opción B: Sistema de Recetas
- **Tabla `recetas`**: id, nombre, descripcion, tiempo_preparacion, dificultad
- **Tabla `ingredientes`**: id, receta_id, nombre, cantidad, unidad
- **Endpoints**: CRUD de recetas + agregar/listar ingredientes

### Opción C: Sistema de Notas/Calificaciones
- **Tabla `alumnos`**: id, nombre, email, grupo
- **Tabla `calificaciones`**: id, alumno_id, materia, calificacion, periodo
- **Endpoints**: CRUD de alumnos + registrar/consultar calificaciones + promedios

---

## Pasos

1. Crea el archivo SQL para la base de datos (`mi_setup.sql`)
2. Crea el archivo de conexión (`mi_config.php`)
3. Crea los endpoints (`mi_api_*.php`)
4. Prueba con cURL o crea un script de pruebas
5. Agrega validaciones y manejo de errores

## Tips

- Empieza simple: primero haz que funcione el GET y POST
- Después agrega PUT y DELETE
- Al final agrega filtros y validaciones
- Revisa los ejemplos del módulo 5 si te atoras

¡Mucho éxito!
