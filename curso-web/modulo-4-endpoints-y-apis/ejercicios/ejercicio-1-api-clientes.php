<?php
// ============================================================
// EJERCICIO 1: Crear una API REST de clientes
// ============================================================
// Crea un endpoint que maneje CRUD de clientes
// Prueba con: php -S localhost:8000
// ============================================================

header('Content-Type: application/json');

require_once '../../modulo-3-php-y-base-de-datos/ejemplos/conexion.php';

// Funciones helper (ya las tienes)
function responder($datos, $codigo = 200) {
    http_response_code($codigo);
    echo json_encode(["status" => "success", "data" => $datos], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit;
}

function responder_error($mensaje, $codigo = 400) {
    http_response_code($codigo);
    echo json_encode(["status" => "error", "message" => $mensaje], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
    exit;
}

$db = obtener_conexion();
$metodo = $_SERVER['REQUEST_METHOD'];
$id = $_GET['id'] ?? null;

switch ($metodo) {

    case 'GET':
        // Si hay $id: devolver UN cliente por ID
        // Si no hay $id: devolver TODOS los clientes
        // Si hay $_GET['ciudad']: filtrar por ciudad
        // TU CÓDIGO AQUÍ:

        break;

    case 'POST':
        // Leer datos JSON del body
        // Validar que vengan 'nombre' y 'email'
        // Insertar en la BD
        // Devolver el cliente creado con código 201
        // TU CÓDIGO AQUÍ:

        break;

    case 'PUT':
        // Validar que venga $id en la URL
        // Verificar que el cliente exista
        // Actualizar con los datos del body
        // Devolver el cliente actualizado
        // TU CÓDIGO AQUÍ:

        break;

    case 'DELETE':
        // Validar que venga $id en la URL
        // Verificar que el cliente exista
        // Eliminar el cliente
        // Devolver mensaje de éxito
        // TU CÓDIGO AQUÍ:

        break;

    default:
        responder_error("Método no permitido", 405);
}
