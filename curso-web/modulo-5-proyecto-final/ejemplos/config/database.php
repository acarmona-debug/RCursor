<?php
// ============================================================
// PROYECTO FINAL: Archivo de configuración de base de datos
// ============================================================

function obtener_conexion() {
    $host = "localhost";
    $dbname = "todo_app";
    $usuario = "root";
    $password = "";

    try {
        $conexion = new PDO(
            "mysql:host=$host;dbname=$dbname;charset=utf8",
            $usuario,
            $password
        );
        $conexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $conexion->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
        return $conexion;
    } catch (PDOException $e) {
        http_response_code(500);
        echo json_encode([
            "status" => "error",
            "message" => "Error de conexión a la base de datos"
        ]);
        exit;
    }
}

function responder($datos, $codigo = 200) {
    http_response_code($codigo);
    echo json_encode(
        ["status" => "success", "data" => $datos],
        JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE
    );
    exit;
}

function responder_error($mensaje, $codigo = 400) {
    http_response_code($codigo);
    echo json_encode(
        ["status" => "error", "message" => $mensaje],
        JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE
    );
    exit;
}

function obtener_body_json() {
    $json = file_get_contents('php://input');
    $datos = json_decode($json, true);
    return $datos;
}
