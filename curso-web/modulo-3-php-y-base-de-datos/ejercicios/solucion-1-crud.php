<?php
// ============================================================
// SOLUCIÓN EJERCICIO 1: CRUD completo de clientes
// ============================================================

require_once '../ejemplos/conexion.php';

$db = obtener_conexion();

function crear_cliente($db, $nombre, $email, $ciudad) {
    $sql = "INSERT INTO clientes (nombre, email, ciudad) VALUES (:nombre, :email, :ciudad)";
    $stmt = $db->prepare($sql);
    $stmt->execute([
        ':nombre' => $nombre,
        ':email'  => $email,
        ':ciudad' => $ciudad
    ]);
    return $db->lastInsertId();
}

function obtener_clientes($db) {
    $stmt = $db->prepare("SELECT * FROM clientes ORDER BY nombre");
    $stmt->execute();
    return $stmt->fetchAll();
}

function buscar_por_email($db, $email) {
    $stmt = $db->prepare("SELECT * FROM clientes WHERE email = :email");
    $stmt->execute([':email' => $email]);
    $resultado = $stmt->fetch();
    return $resultado ?: null;
}

function actualizar_email($db, $id, $nuevo_email) {
    $sql = "UPDATE clientes SET email = :email WHERE id = :id";
    $stmt = $db->prepare($sql);
    $stmt->execute([':email' => $nuevo_email, ':id' => $id]);
    return $stmt->rowCount();
}

function eliminar_cliente($db, $id) {
    $stmt = $db->prepare("DELETE FROM clientes WHERE id = :id");
    $stmt->execute([':id' => $id]);
    return $stmt->rowCount();
}

function contar_por_ciudad($db, $ciudad) {
    $stmt = $db->prepare("SELECT COUNT(*) as total FROM clientes WHERE ciudad = :ciudad");
    $stmt->execute([':ciudad' => $ciudad]);
    $resultado = $stmt->fetch();
    return $resultado['total'];
}

// PRUEBAS
echo "--- Crear cliente ---\n";
$id = crear_cliente($db, "Test User", "test@email.com", "CDMX");
echo "Cliente creado con ID: $id\n";

echo "\n--- Todos los clientes ---\n";
$clientes = obtener_clientes($db);
foreach ($clientes as $c) {
    echo "  [{$c['id']}] {$c['nombre']} - {$c['email']} ({$c['ciudad']})\n";
}

echo "\n--- Buscar por email ---\n";
$cliente = buscar_por_email($db, "test@email.com");
echo $cliente ? "Encontrado: {$cliente['nombre']}\n" : "No encontrado\n";

echo "\n--- Actualizar email ---\n";
$filas = actualizar_email($db, $id, "nuevo@email.com");
echo "Filas afectadas: $filas\n";

echo "\n--- Contar por ciudad ---\n";
echo "Clientes en CDMX: " . contar_por_ciudad($db, "CDMX") . "\n";

echo "\n--- Eliminar cliente ---\n";
$filas = eliminar_cliente($db, $id);
echo "Eliminados: $filas\n";
