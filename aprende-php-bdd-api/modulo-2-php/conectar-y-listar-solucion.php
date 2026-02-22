<?php
// SOLUCIÓN Ejercicio 2.2

try {
    // 1. Conectar a SQLite
    $pdo = new PDO('sqlite:datos.db');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    // 2. Crear tabla usuarios si no existe
    $pdo->exec("CREATE TABLE IF NOT EXISTS usuarios (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nombre TEXT,
        email TEXT
    )");

    // 3. Insertar un usuario de prueba
    $stmt = $pdo->prepare("INSERT INTO usuarios (nombre, email) VALUES (?, ?)");
    $stmt->execute(["Usuario Prueba", "prueba@email.com"]);

    // 4. Listar todos los usuarios
    $usuarios = $pdo->query("SELECT * FROM usuarios")->fetchAll(PDO::FETCH_ASSOC);

    echo "=== Usuarios en la base de datos ===\n\n";
    foreach ($usuarios as $u) {
        echo "ID: {$u['id']} | {$u['nombre']} | {$u['email']}\n";
    }

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage();
}
?>
