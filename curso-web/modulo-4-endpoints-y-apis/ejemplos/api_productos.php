<?php
// ============================================================
// EJEMPLO: API REST completa de productos
// ============================================================
// Para probar: php -S localhost:8000
// Luego usa curl o un navegador
// ============================================================

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

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

    // ==========================================
    // GET: Leer productos
    // ==========================================
    case 'GET':
        if ($id) {
            $stmt = $db->prepare("SELECT * FROM productos WHERE id = :id");
            $stmt->execute([':id' => $id]);
            $producto = $stmt->fetch();

            if ($producto) {
                responder($producto);
            } else {
                responder_error("Producto no encontrado", 404);
            }
        } else {
            $categoria = $_GET['categoria'] ?? null;

            if ($categoria) {
                $stmt = $db->prepare("SELECT * FROM productos WHERE categoria = :cat ORDER BY nombre");
                $stmt->execute([':cat' => $categoria]);
            } else {
                $stmt = $db->prepare("SELECT * FROM productos ORDER BY nombre");
                $stmt->execute();
            }

            $productos = $stmt->fetchAll();
            responder($productos);
        }
        break;

    // ==========================================
    // POST: Crear producto
    // ==========================================
    case 'POST':
        $json = file_get_contents('php://input');
        $datos = json_decode($json, true);

        if (!$datos || empty($datos['nombre']) || !isset($datos['precio'])) {
            responder_error("Se requiere 'nombre' y 'precio'");
        }

        $sql = "INSERT INTO productos (nombre, precio, cantidad_en_stock, categoria) VALUES (:nombre, :precio, :stock, :categoria)";
        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':nombre'    => $datos['nombre'],
            ':precio'    => $datos['precio'],
            ':stock'     => $datos['cantidad_en_stock'] ?? 0,
            ':categoria' => $datos['categoria'] ?? null
        ]);

        $nuevo_id = $db->lastInsertId();

        $stmt = $db->prepare("SELECT * FROM productos WHERE id = :id");
        $stmt->execute([':id' => $nuevo_id]);
        $producto = $stmt->fetch();

        responder($producto, 201);
        break;

    // ==========================================
    // PUT: Actualizar producto
    // ==========================================
    case 'PUT':
        if (!$id) {
            responder_error("Se requiere ?id= en la URL");
        }

        $json = file_get_contents('php://input');
        $datos = json_decode($json, true);

        if (!$datos) {
            responder_error("Se requieren datos en formato JSON");
        }

        $stmt = $db->prepare("SELECT * FROM productos WHERE id = :id");
        $stmt->execute([':id' => $id]);
        $producto = $stmt->fetch();

        if (!$producto) {
            responder_error("Producto no encontrado", 404);
        }

        $sql = "UPDATE productos SET
                nombre = :nombre,
                precio = :precio,
                cantidad_en_stock = :stock,
                categoria = :categoria
                WHERE id = :id";

        $stmt = $db->prepare($sql);
        $stmt->execute([
            ':id'        => $id,
            ':nombre'    => $datos['nombre'] ?? $producto['nombre'],
            ':precio'    => $datos['precio'] ?? $producto['precio'],
            ':stock'     => $datos['cantidad_en_stock'] ?? $producto['cantidad_en_stock'],
            ':categoria' => $datos['categoria'] ?? $producto['categoria']
        ]);

        $stmt = $db->prepare("SELECT * FROM productos WHERE id = :id");
        $stmt->execute([':id' => $id]);
        responder($stmt->fetch());
        break;

    // ==========================================
    // DELETE: Eliminar producto
    // ==========================================
    case 'DELETE':
        if (!$id) {
            responder_error("Se requiere ?id= en la URL");
        }

        $stmt = $db->prepare("SELECT * FROM productos WHERE id = :id");
        $stmt->execute([':id' => $id]);

        if (!$stmt->fetch()) {
            responder_error("Producto no encontrado", 404);
        }

        $stmt = $db->prepare("DELETE FROM productos WHERE id = :id");
        $stmt->execute([':id' => $id]);

        responder(["message" => "Producto eliminado correctamente"]);
        break;

    default:
        responder_error("Método no permitido", 405);
}
