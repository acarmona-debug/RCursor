<?php
// ============================================================
// PROYECTO FINAL: API de Tareas (ejemplo completo)
// ============================================================

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once __DIR__ . '/../config/database.php';

$db = obtener_conexion();
$metodo = $_SERVER['REQUEST_METHOD'];
$id = $_GET['id'] ?? null;

switch ($metodo) {

    // ==========================================
    // GET: Listar tareas (con filtros opcionales)
    // ==========================================
    case 'GET':
        if ($id) {
            $stmt = $db->prepare("
                SELECT t.*, u.nombre as usuario_nombre
                FROM tareas t
                JOIN usuarios u ON t.usuario_id = u.id
                WHERE t.id = :id
            ");
            $stmt->execute([':id' => $id]);
            $tarea = $stmt->fetch();

            if ($tarea) {
                responder($tarea);
            } else {
                responder_error("Tarea no encontrada", 404);
            }
        } else {
            $where = [];
            $params = [];

            if (!empty($_GET['usuario_id'])) {
                $where[] = "t.usuario_id = :usuario_id";
                $params[':usuario_id'] = $_GET['usuario_id'];
            }

            if (!empty($_GET['estado'])) {
                $where[] = "t.estado = :estado";
                $params[':estado'] = $_GET['estado'];
            }

            if (!empty($_GET['prioridad'])) {
                $where[] = "t.prioridad = :prioridad";
                $params[':prioridad'] = $_GET['prioridad'];
            }

            $sql = "SELECT t.*, u.nombre as usuario_nombre
                    FROM tareas t
                    JOIN usuarios u ON t.usuario_id = u.id";

            if (!empty($where)) {
                $sql .= " WHERE " . implode(" AND ", $where);
            }

            $sql .= " ORDER BY
                FIELD(t.prioridad, 'alta', 'media', 'baja'),
                t.fecha_limite ASC";

            $stmt = $db->prepare($sql);
            $stmt->execute($params);
            responder($stmt->fetchAll());
        }
        break;

    // ==========================================
    // POST: Crear una tarea
    // ==========================================
    case 'POST':
        $datos = obtener_body_json();

        if (!$datos || empty($datos['titulo']) || empty($datos['usuario_id'])) {
            responder_error("Se requiere 'titulo' y 'usuario_id'");
        }

        $check = $db->prepare("SELECT id FROM usuarios WHERE id = :id");
        $check->execute([':id' => $datos['usuario_id']]);
        if (!$check->fetch()) {
            responder_error("El usuario no existe", 404);
        }

        $estados_validos = ['pendiente', 'en_progreso', 'completada'];
        $estado = $datos['estado'] ?? 'pendiente';
        if (!in_array($estado, $estados_validos)) {
            responder_error("Estado inválido. Opciones: " . implode(', ', $estados_validos));
        }

        $prioridades_validas = ['baja', 'media', 'alta'];
        $prioridad = $datos['prioridad'] ?? 'media';
        if (!in_array($prioridad, $prioridades_validas)) {
            responder_error("Prioridad inválida. Opciones: " . implode(', ', $prioridades_validas));
        }

        $sql = "INSERT INTO tareas (usuario_id, titulo, descripcion, estado, prioridad, fecha_limite)
                VALUES (:usuario_id, :titulo, :descripcion, :estado, :prioridad, :fecha_limite)";

        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':usuario_id'  => $datos['usuario_id'],
            ':titulo'      => $datos['titulo'],
            ':descripcion' => $datos['descripcion'] ?? null,
            ':estado'      => $estado,
            ':prioridad'   => $prioridad,
            ':fecha_limite' => $datos['fecha_limite'] ?? null
        ]);

        $nuevo_id = $db->lastInsertId();
        $stmt = $db->prepare("SELECT t.*, u.nombre as usuario_nombre FROM tareas t JOIN usuarios u ON t.usuario_id = u.id WHERE t.id = :id");
        $stmt->execute([':id' => $nuevo_id]);

        responder($stmt->fetch(), 201);
        break;

    // ==========================================
    // PUT: Actualizar una tarea
    // ==========================================
    case 'PUT':
        if (!$id) {
            responder_error("Se requiere ?id= en la URL");
        }

        $datos = obtener_body_json();
        if (!$datos) {
            responder_error("Se requieren datos en formato JSON");
        }

        $stmt = $db->prepare("SELECT * FROM tareas WHERE id = :id");
        $stmt->execute([':id' => $id]);
        $tarea = $stmt->fetch();

        if (!$tarea) {
            responder_error("Tarea no encontrada", 404);
        }

        $sql = "UPDATE tareas SET
                titulo = :titulo,
                descripcion = :descripcion,
                estado = :estado,
                prioridad = :prioridad,
                fecha_limite = :fecha_limite
                WHERE id = :id";

        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':id'           => $id,
            ':titulo'       => $datos['titulo'] ?? $tarea['titulo'],
            ':descripcion'  => $datos['descripcion'] ?? $tarea['descripcion'],
            ':estado'       => $datos['estado'] ?? $tarea['estado'],
            ':prioridad'    => $datos['prioridad'] ?? $tarea['prioridad'],
            ':fecha_limite' => $datos['fecha_limite'] ?? $tarea['fecha_limite']
        ]);

        $stmt = $db->prepare("SELECT t.*, u.nombre as usuario_nombre FROM tareas t JOIN usuarios u ON t.usuario_id = u.id WHERE t.id = :id");
        $stmt->execute([':id' => $id]);

        responder($stmt->fetch());
        break;

    // ==========================================
    // DELETE: Eliminar una tarea
    // ==========================================
    case 'DELETE':
        if (!$id) {
            responder_error("Se requiere ?id= en la URL");
        }

        $stmt = $db->prepare("SELECT * FROM tareas WHERE id = :id");
        $stmt->execute([':id' => $id]);

        if (!$stmt->fetch()) {
            responder_error("Tarea no encontrada", 404);
        }

        $stmt = $db->prepare("DELETE FROM tareas WHERE id = :id");
        $stmt->execute([':id' => $id]);

        responder(["message" => "Tarea eliminada correctamente"]);
        break;

    default:
        responder_error("Método no permitido", 405);
}
