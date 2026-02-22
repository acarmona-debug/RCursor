<?php
// ============================================================
// PROYECTO FINAL: API de Estadísticas
// ============================================================

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

require_once __DIR__ . '/../config/database.php';

$db = obtener_conexion();

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    responder_error("Solo se permite GET", 405);
}

$stats = [];

$stmt = $db->prepare("SELECT COUNT(*) as total FROM tareas");
$stmt->execute();
$stats['total_tareas'] = $stmt->fetch()['total'];

$stmt = $db->prepare("
    SELECT estado, COUNT(*) as cantidad
    FROM tareas
    GROUP BY estado
");
$stmt->execute();
$stats['por_estado'] = $stmt->fetchAll();

$stmt = $db->prepare("
    SELECT prioridad, COUNT(*) as cantidad
    FROM tareas
    GROUP BY prioridad
    ORDER BY FIELD(prioridad, 'alta', 'media', 'baja')
");
$stmt->execute();
$stats['por_prioridad'] = $stmt->fetchAll();

$stmt = $db->prepare("
    SELECT
        u.nombre,
        u.email,
        COUNT(t.id) as total_tareas,
        SUM(CASE WHEN t.estado = 'completada' THEN 1 ELSE 0 END) as completadas,
        SUM(CASE WHEN t.estado = 'en_progreso' THEN 1 ELSE 0 END) as en_progreso,
        SUM(CASE WHEN t.estado = 'pendiente' THEN 1 ELSE 0 END) as pendientes
    FROM usuarios u
    LEFT JOIN tareas t ON u.id = t.usuario_id
    GROUP BY u.id, u.nombre, u.email
    ORDER BY total_tareas DESC
");
$stmt->execute();
$stats['por_usuario'] = $stmt->fetchAll();

$stmt = $db->prepare("
    SELECT COUNT(*) as vencidas
    FROM tareas
    WHERE fecha_limite < CURDATE() AND estado != 'completada'
");
$stmt->execute();
$stats['tareas_vencidas'] = $stmt->fetch()['vencidas'];

responder($stats);
