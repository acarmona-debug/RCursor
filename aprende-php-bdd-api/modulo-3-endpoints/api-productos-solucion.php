<?php
/**
 * SOLUCIÓN Ejercicio 3: API de productos por precio mínimo
 */

header('Content-Type: application/json; charset=utf-8');

$precioMinimo = (float) ($_GET['precio_minimo'] ?? 0);

try {
    $pdo = new PDO('sqlite:' . __DIR__ . '/productos.db');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // Crear tabla y datos de prueba si no existen
    $pdo->exec("CREATE TABLE IF NOT EXISTS productos (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nombre TEXT,
        precio REAL,
        cantidad INTEGER
    )");

    // Insertar datos de prueba (solo si está vacía)
    $count = $pdo->query("SELECT COUNT(*) FROM productos")->fetchColumn();
    if ($count == 0) {
        $pdo->exec("INSERT INTO productos (nombre, precio, cantidad) VALUES 
            ('Laptop', 999.99, 10),
            ('Mouse', 29.50, 50),
            ('Teclado', 79.00, 25)");
    }

    $stmt = $pdo->prepare("SELECT * FROM productos WHERE precio >= ? ORDER BY precio DESC");
    $stmt->execute([$precioMinimo]);
    $productos = $stmt->fetchAll(PDO::FETCH_ASSOC);

    echo json_encode([
        'success' => true,
        'data' => $productos
    ]);

} catch (PDOException $e) {
    http_response_code(500);
    echo json_encode(['success' => false, 'error' => $e->getMessage()]);
}
