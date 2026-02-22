<?php
/**
 * API con Base de Datos
 * 
 * Endpoints:
 * GET  ?action=listar     → Lista todos los usuarios
 * GET  ?action=ver&id=1   → Ver usuario con id 1
 * POST (body JSON)       → Crear usuario
 * 
 * Iniciar servidor: php -S localhost:8000
 * Probar: curl http://localhost:8000/api-con-bdd.php?action=listar
 */

header('Content-Type: application/json; charset=utf-8');

try {
    $dbPath = __DIR__ . '/../modulo-2-php/datos.db';
// Crear DB si no existe: ejecuta primero conectar-y-listar-solucion.php
$pdo = new PDO('sqlite:' . $dbPath);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => 'Error de conexión']);
    exit;
}

$method = $_SERVER['REQUEST_METHOD'];
$action = $_GET['action'] ?? '';

// GET - Listar o ver uno
if ($method === 'GET') {
    if ($action === 'listar') {
        $stmt = $pdo->query("SELECT * FROM usuarios");
        $usuarios = $stmt->fetchAll(PDO::FETCH_ASSOC);
        echo json_encode(['success' => true, 'data' => $usuarios]);
        exit;
    }

    if ($action === 'ver' && isset($_GET['id'])) {
        $id = (int) $_GET['id'];
        $stmt = $pdo->prepare("SELECT * FROM usuarios WHERE id = ?");
        $stmt->execute([$id]);
        $usuario = $stmt->fetch(PDO::FETCH_ASSOC);
        if ($usuario) {
            echo json_encode(['success' => true, 'data' => $usuario]);
        } else {
            http_response_code(404);
            echo json_encode(['success' => false, 'error' => 'Usuario no encontrado']);
        }
        exit;
    }
}

// POST - Crear usuario
if ($method === 'POST') {
    $input = json_decode(file_get_contents('php://input'), true) ?? $_POST;
    $nombre = $input['nombre'] ?? '';
    $email = $input['email'] ?? '';

    if (empty($nombre) || empty($email)) {
        http_response_code(400);
        echo json_encode(['success' => false, 'error' => 'Faltan nombre o email']);
        exit;
    }

    $stmt = $pdo->prepare("INSERT INTO usuarios (nombre, email) VALUES (?, ?)");
    $stmt->execute([$nombre, $email]);
    $id = $pdo->lastInsertId();

    http_response_code(201);
    echo json_encode([
        'success' => true,
        'mensaje' => 'Usuario creado',
        'data' => ['id' => $id, 'nombre' => $nombre, 'email' => $email]
    ]);
    exit;
}

// Si no coincide nada
http_response_code(400);
echo json_encode(['success' => false, 'error' => 'Petición no válida']);
