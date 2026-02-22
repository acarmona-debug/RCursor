-- ============================================================
-- PROYECTO FINAL: Setup de la base de datos
-- Ejecuta con: mysql -u root < setup.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS todo_app;
USE todo_app;

DROP TABLE IF EXISTS tareas;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tareas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    estado ENUM('pendiente', 'en_progreso', 'completada') DEFAULT 'pendiente',
    prioridad ENUM('baja', 'media', 'alta') DEFAULT 'media',
    fecha_limite DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

INSERT INTO usuarios (nombre, email) VALUES
    ('Ana García', 'ana@email.com'),
    ('Carlos López', 'carlos@email.com'),
    ('María Torres', 'maria@email.com');

INSERT INTO tareas (usuario_id, titulo, descripcion, estado, prioridad, fecha_limite) VALUES
    (1, 'Estudiar PHP', 'Completar el módulo 2 del curso', 'completada', 'alta', '2026-02-20'),
    (1, 'Crear base de datos', 'Diseñar tablas del proyecto', 'en_progreso', 'alta', '2026-02-25'),
    (1, 'Hacer ejercicios SQL', 'Resolver los 3 ejercicios del módulo 1', 'pendiente', 'media', '2026-02-28'),
    (2, 'Aprender APIs', 'Leer la teoría del módulo 4', 'pendiente', 'alta', '2026-03-01'),
    (2, 'Proyecto final', 'Construir la API de tareas', 'pendiente', 'alta', '2026-03-05'),
    (3, 'Configurar MySQL', 'Instalar y configurar la base de datos', 'completada', 'media', '2026-02-18'),
    (3, 'Practicar PHP', 'Resolver ejercicios de funciones', 'en_progreso', 'media', '2026-02-27');

SELECT 'Base de datos todo_app creada correctamente' AS mensaje;
