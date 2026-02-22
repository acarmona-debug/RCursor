<?php
// ============================================================
// EJEMPLO: CRUD completo con PHP y MySQL
// ============================================================
// Este archivo muestra las 4 operaciones básicas (CRUD)
// Necesitas tener MySQL corriendo y la base de datos "tienda"
// ============================================================

require_once 'conexion.php';

$db = obtener_conexion();

// ----------------------------------------------------------
// CREATE: Insertar un producto
// ----------------------------------------------------------
function crear_producto($db, $nombre, $precio, $stock, $categoria) {
    $sql = "INSERT INTO productos (nombre, precio, cantidad_en_stock, categoria)
            VALUES (:nombre, :precio, :stock, :categoria)";

    $stmt = $db->prepare($sql);
    $stmt->execute([
        ':nombre'    => $nombre,
        ':precio'    => $precio,
        ':stock'     => $stock,
        ':categoria' => $categoria
    ]);

    return $db->lastInsertId();
}

// ----------------------------------------------------------
// READ: Obtener productos
// ----------------------------------------------------------
function obtener_todos_productos($db) {
    $stmt = $db->prepare("SELECT * FROM productos ORDER BY nombre");
    $stmt->execute();
    return $stmt->fetchAll();
}

function obtener_producto_por_id($db, $id) {
    $stmt = $db->prepare("SELECT * FROM productos WHERE id = :id");
    $stmt->execute([':id' => $id]);
    return $stmt->fetch();
}

function buscar_productos($db, $termino) {
    $stmt = $db->prepare("SELECT * FROM productos WHERE nombre LIKE :termino");
    $stmt->execute([':termino' => "%$termino%"]);
    return $stmt->fetchAll();
}

// ----------------------------------------------------------
// UPDATE: Actualizar un producto
// ----------------------------------------------------------
function actualizar_producto($db, $id, $nombre, $precio, $stock) {
    $sql = "UPDATE productos SET nombre = :nombre, precio = :precio,
            cantidad_en_stock = :stock WHERE id = :id";

    $stmt = $db->prepare($sql);
    $stmt->execute([
        ':id'     => $id,
        ':nombre' => $nombre,
        ':precio' => $precio,
        ':stock'  => $stock
    ]);

    return $stmt->rowCount();
}

// ----------------------------------------------------------
// DELETE: Eliminar un producto
// ----------------------------------------------------------
function eliminar_producto($db, $id) {
    $stmt = $db->prepare("DELETE FROM productos WHERE id = :id");
    $stmt->execute([':id' => $id]);
    return $stmt->rowCount();
}

// ----------------------------------------------------------
// EJEMPLO DE USO
// ----------------------------------------------------------

echo "=== CRUD de Productos ===\n\n";

// CREATE
echo "--- Insertando productos ---\n";
$id1 = crear_producto($db, "Webcam HD", 650.00, 12, "Electrónica");
echo "Producto creado con ID: $id1\n";

$id2 = crear_producto($db, "Hub USB", 380.00, 30, "Accesorios");
echo "Producto creado con ID: $id2\n";

// READ
echo "\n--- Todos los productos ---\n";
$productos = obtener_todos_productos($db);
foreach ($productos as $p) {
    echo "  [{$p['id']}] {$p['nombre']} - \${$p['precio']} (Stock: {$p['cantidad_en_stock']})\n";
}

echo "\n--- Buscar 'web' ---\n";
$resultados = buscar_productos($db, "web");
foreach ($resultados as $p) {
    echo "  Encontrado: {$p['nombre']}\n";
}

// UPDATE
echo "\n--- Actualizando producto $id1 ---\n";
$filas = actualizar_producto($db, $id1, "Webcam Full HD", 750.00, 10);
echo "$filas producto(s) actualizado(s)\n";

$actualizado = obtener_producto_por_id($db, $id1);
echo "Ahora es: {$actualizado['nombre']} - \${$actualizado['precio']}\n";

// DELETE
echo "\n--- Eliminando producto $id2 ---\n";
$filas = eliminar_producto($db, $id2);
echo "$filas producto(s) eliminado(s)\n";

echo "\n--- Productos finales ---\n";
$productos = obtener_todos_productos($db);
foreach ($productos as $p) {
    echo "  [{$p['id']}] {$p['nombre']} - \${$p['precio']}\n";
}
