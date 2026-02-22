<?php
/**
 * Script de configuración inicial
 * Ejecuta esto primero para crear las bases de datos necesarias
 * 
 * php setup-inicial.php
 */

echo "=== Configuración inicial del curso ===\n\n";

// Crear datos.db (usuarios)
$dirMod2 = __DIR__ . '/modulo-2-php';
if (!is_dir($dirMod2)) {
    mkdir($dirMod2, 0755, true);
}

try {
    $pdo = new PDO('sqlite:' . $dirMod2 . '/datos.db');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $pdo->exec("CREATE TABLE IF NOT EXISTS usuarios (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        nombre TEXT,
        email TEXT
    )");
    $pdo->exec("INSERT OR IGNORE INTO usuarios (id, nombre, email) VALUES (1, 'Usuario Inicial', 'inicial@test.com')");
    echo "✓ Base de datos usuarios creada en modulo-2-php/datos.db\n";
} catch (Exception $e) {
    echo "✗ Error usuarios: " . $e->getMessage() . "\n";
}

echo "\n¡Listo! Ahora puedes seguir con el curso.\n";
