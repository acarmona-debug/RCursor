<?php
// ============================================================
// SOLUCIÓN EJERCICIO 1: API REST de clientes
// ============================================================

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type');

require_once '../../modulo-3-php-y-base-de-datos/ejemplos/conexion.php';

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
        if ($id) {
            $stmt = $db->prepare("SELECT * FROM clientes WHERE id = :id");
            $stmt->execute([':id' => $id]);
            $cliente = $stmt->fetch();

            if ($cliente) {
                responder($cliente);
            } else {
                responder_error("Cliente no encontrado", 404);
            }
        } else {
            $ciudad = $_GET['ciudad'] ?? null;

            if ($ciudad) {
                $stmt = $db->prepare("SELECT * FROM clientes WHERE ciudad = :ciudad ORDER BY nombre");
                $stmt->execute([':ciudad' => $ciudad]);
            } else {
                $stmt = $db->prepare("SELECT * FROM clientes ORDER BY nombre");
                $stmt->execute();
            }

            responder($stmt->fetchAll());
        }
        break;

    case 'POST':
        $json = file_get_contents('php://input');
        $datos = json_decode($json, true);

        if (!$datos || empty($datos['nombre']) || empty($datos['email'])) {
            responder_error("Se requiere 'nombre' y 'email'");
        }

        $check = $db->prepare("SELECT id FROM clientes WHERE email = :email");
        $check->execute([':email' => $datos['email']]);
        if ($check->fetch()) {
            responder_error("Ya existe un cliente con ese email", 409);
        }

        $sql = "INSERT INTO clientes (nombre, email, ciudad) VALUES (:nombre, :email, :ciudad)";
        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':nombre' => $datos['nombre'],
            ':email'  => $datos['email'],
            ':ciudad' => $datos['ciudad'] ?? null
        ]);

        $nuevo_id = $db->lastInsertId();
        $stmt = $db->prepare("SELECT * FROM clientes WHERE id = :id");
        $stmt->execute([':id' => $nuevo_id]);

        responder($stmt->fetch(), 201);
        break;

    case 'PUT':
        if (!$id) {
            responder_error("Se requiere ?id= en la URL");
        }

        $json = file_get_contents('php://input');
        $datos = json_decode($json, true);

        if (!$datos) {
            responder_error("Se requieren datos en formato JSON");
        }

        $stmt = $db->prepare("SELECT * FROM clientes WHERE id = :id");
        $stmt->execute([':id' => $id]);
        $cliente = $stmt->fetch();

        if (!$cliente) {
            responder_error("Cliente no encontrado", 404);
        }

        $sql = "UPDATE clientes SET nombre = :nombre, email = :email, ciudad = :ciudad WHERE id = :id";
        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':id'     => $id,
            ':nombre' => $datos['nombre'] ?? $cliente['nombre'],
            ':email'  => $datos['email'] ?? $cliente['email'],
            ':ciudad' => $datos['ciudad'] ?? $cliente['ciudad']
        ]);

        $stmt = $db->prepare("SELECT * FROM clientes WHERE id = :id");
        $stmt->execute([':id' => $id]);
        responder($stmt->fetch());
        break;

    case 'DELETE':
        if (!$id) {
            responder_error("Se requiere ?id= en la URL");
        }

        $stmt = $db->prepare("SELECT * FROM clientes WHERE id = :id");
        $stmt->execute([':id' => $id]);

        if (!$stmt->fetch()) {
            responder_error("Cliente no encontrado", 404);
        }

        $stmt = $db->prepare("DELETE FROM clientes WHERE id = :id");
        $stmt->execute([':id' => $id]);

        responder(["message" => "Cliente eliminado correctamente"]);
        break;

    default:
        responder_error("Método no permitido", 405);
}
