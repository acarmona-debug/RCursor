# Bootcamp backend desde cero: PHP + Bases de datos + Endpoints

Este mini bootcamp esta pensado para alguien que empieza de cero.
Tu objetivo es entender como viajan los datos en web y construir endpoints basicos.

## Como vamos a trabajar tu y yo

1. Estudias un modulo corto.
2. Resuelves los ejercicios clave.
3. Me respondes por chat:
   - "Hecho M0", "Hecho M1", etc.
   - Tu respuesta o codigo.
4. Yo corrijo, te doy feedback y desbloqueo el siguiente paso.

## Requisitos recomendados

- PHP 8.2+
- SQLite (normalmente viene con PHP)
- curl
- Un editor de codigo

En esta maquina de cloud no hay PHP instalado, pero los ejercicios estan listos para correrlos en tu equipo local.

## Inicio rapido

Desde la raiz del repo:

```bash
cd aprendizaje-backend
```

## Modo movil (sin volver al chat en cada pregunta)

Tienes una version interactiva con autoevaluacion en:

- `web/curso-interactivo.html`

Opciones para abrirla:

1. Abrir el archivo directo en el navegador del celular.
2. O servirlo desde tu PC y entrar desde el celular:

```bash
python3 -m http.server 8080 --directory web
```

Luego abre en el celular:

- `http://<IP_DE_TU_PC>:8080/curso-interactivo.html`

La pagina guarda avance en el navegador (localStorage), corrige respuestas y te muestra feedback inmediato.

## Estructura del curso

1. **M0 - Fundamentos web y HTTP**
   - Archivo: `ejercicios/00_http_fundamentos.md`
2. **M1 - PHP basico**
   - Archivo: `ejercicios/01_php_basico.php`
3. **M2 - Funciones, arrays y logica en PHP**
   - Archivo: `ejercicios/02_php_funciones_arrays.php`
4. **M3 - Base de datos con SQLite y PDO**
   - Archivo: `ejercicios/03_sqlite_pdo.php`
5. **M4 - Endpoints basicos con PHP**
   - Archivo: `ejercicios/api/index.php`
6. **M5 - SQL de practica**
   - Archivo: `ejercicios/04_sql_practica.sql`
7. **Modo interactivo para celular**
   - Archivo: `web/curso-interactivo.html`

## Ruta sugerida (sin saltos)

### M0 - Fundamentos web y HTTP

Aprenderas:
- Que es cliente, servidor, request y response
- Metodos HTTP (GET, POST, PUT, DELETE)
- Codigos de estado (200, 201, 400, 404, 500)

Salida esperada:
- Explicar en tus palabras que pasa cuando visitas una URL.

### M1 - PHP basico

Aprenderas:
- Variables y tipos
- Condiciones
- Bucles
- Salida en consola con `echo`

Comando:

```bash
php ejercicios/01_php_basico.php
```

### M2 - Funciones y arrays

Aprenderas:
- Crear funciones
- Recorrer arrays
- Separar logica en bloques reutilizables

Comando:

```bash
php ejercicios/02_php_funciones_arrays.php
```

### M3 - SQLite + PDO

Aprenderas:
- Crear tabla
- Insertar filas
- Consultar datos
- Usar consultas preparadas (seguridad basica)

Comando:

```bash
php ejercicios/03_sqlite_pdo.php
```

### M4 - Endpoints basicos

Aprenderas:
- Leer metodo HTTP y ruta
- Responder JSON
- Hacer GET y POST

Servidor local:

```bash
php -S localhost:8000 ejercicios/api/index.php
```

Llamadas de prueba:

```bash
curl -i http://localhost:8000/health
curl -i http://localhost:8000/users
curl -i -X POST http://localhost:8000/users -H "Content-Type: application/json" -d '{"name":"Ana","email":"ana@example.com"}'
```

### M5 - SQL de practica

Aprenderas:
- SELECT con filtros
- ORDER BY
- LIMIT
- UPDATE y DELETE

## Reglas de oro (backend basico)

1. Nunca confies en la entrada del usuario.
2. Valida datos antes de guardar.
3. Usa consultas preparadas en SQL.
4. Responde errores claros en JSON.
5. Empieza simple y mejora por iteraciones.

## Checklist de avance

- [ ] M0 completado
- [ ] M1 completado
- [ ] M2 completado
- [ ] M3 completado
- [ ] M4 completado
- [ ] M5 completado

## Primer paso ahora

Abre `ejercicios/00_http_fundamentos.md` y responde las preguntas.
Cuando termines, escribeme: **"Hecho M0"** con tus respuestas.
