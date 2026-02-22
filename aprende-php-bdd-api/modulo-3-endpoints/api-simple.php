<?php
/**
 * API Simple de Ejemplo
 * 
 * Cómo probar:
 * 1. Inicia el servidor: php -S localhost:8000
 * 2. Abre en el navegador: http://localhost:8000/api-simple.php
 * 3. O con curl: curl http://localhost:8000/api-simple.php
 */

// Decir que la respuesta es JSON
header('Content-Type: application/json; charset=utf-8');

// Datos de ejemplo (en un proyecto real vendrían de la base de datos)
$usuarios = [
    ['id' => 1, 'nombre' => 'Ana', 'email' => 'ana@email.com'],
    ['id' => 2, 'nombre' => 'Bruno', 'email' => 'bruno@email.com'],
    ['id' => 3, 'nombre' => 'Clara', 'email' => 'clara@email.com'],
];

// Responder con JSON
http_response_code(200);
echo json_encode([
    'success' => true,
    'mensaje' => 'Lista de usuarios',
    'data' => $usuarios
]);
