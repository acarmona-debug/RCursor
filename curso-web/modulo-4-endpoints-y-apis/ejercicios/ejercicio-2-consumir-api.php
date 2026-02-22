<?php
// ============================================================
// EJERCICIO 2: Consumir una API externa
// ============================================================
// Usa la API pública: https://jsonplaceholder.typicode.com
// Ejecuta con: php ejercicio-2-consumir-api.php
// ============================================================

// PARTE A: Obtener los primeros 5 posts
// URL: https://jsonplaceholder.typicode.com/posts
// Muestra: id, título (title) de los primeros 5
// Pista: usa file_get_contents() y json_decode()
echo "--- Parte A: Primeros 5 posts ---\n";
// TU CÓDIGO AQUÍ:



// PARTE B: Obtener info de un usuario específico
// URL: https://jsonplaceholder.typicode.com/users/3
// Muestra: nombre (name), email, ciudad (city dentro de address)
echo "\n--- Parte B: Info del usuario 3 ---\n";
// TU CÓDIGO AQUÍ:



// PARTE C: Crear un nuevo post usando cURL
// URL: https://jsonplaceholder.typicode.com/posts
// Método: POST
// Datos: title, body, userId
// Muestra el id del post creado
echo "\n--- Parte C: Crear post ---\n";
// TU CÓDIGO AQUÍ:



// PARTE D: Obtener los comentarios del post 1
// URL: https://jsonplaceholder.typicode.com/posts/1/comments
// Muestra: nombre (name) y email de cada comentario
echo "\n--- Parte D: Comentarios del post 1 ---\n";
// TU CÓDIGO AQUÍ:



// PARTE E: Crear una función reutilizable para llamadas GET
// Nombre: llamar_api($url)
// Debe: hacer GET, decodificar JSON, devolver el resultado
// Si hay error, devolver null
echo "\n--- Parte E: Función reutilizable ---\n";
// TU CÓDIGO AQUÍ:



// Prueba la función:
// $todos = llamar_api("https://jsonplaceholder.typicode.com/todos?userId=1&completed=true");
// echo "Tareas completadas del usuario 1: " . count($todos) . "\n";
