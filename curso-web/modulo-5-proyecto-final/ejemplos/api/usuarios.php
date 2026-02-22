<?php
// ============================================================
// PROYECTO FINAL: API de Usuarios (ejemplo completo)
// ============================================================

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST');
header('Access-Control-Allow-Headers: Content-Type');

require_once __DIR__ . '/../config/database.php';

$db = obtener_conexion();
$metodo = $_SERVER['REQUEST_METHOD'];

switch ($metodo) {

    case 'GET':
        $stmt = $db->prepare("SELECT * FROM usuarios ORDER BY nombre");
        $stmt->execute();
        responder($stmt->fetchAll());
        break;

    case 'POST':
        $datos = obtener_body_json();

        if (!$datos || empty($datos['nombre']) || empty($datos['email'])) {
            responder_error("Se requiere 'nombre' y 'email'");
        }

        $check = $db->prepare("SELECT id FROM usuarios WHERE email = :email");
        $check->execute([':email' => $datos['email']]);
        if ($check->fetch()) {
            responder_error("Ya existe un usuario con ese email", 409);
        }

        $sql = "INSERT INTO usuarios (nombre, email) VALUES (:nombre, :email)";
        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':nombre' => $datos['nombre'],
            ':email'  => $datos['email']
        ]);

        $id = $db->lastInsertId();
        $stmt = $db->prepare("SELECT * FROM usuarios WHERE id = :id");
        $stmt->execute([':id' => $id]);

        responder($stmt->fetch(), 201);
        break;

    default:
        responder_error("Método no permitido", 405);
}
