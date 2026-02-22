<?php

declare(strict_types=1);

echo "=== M3: SQLite + PDO ===\n";

$dbPath = __DIR__ . "/curso.db";
$pdo = new PDO("sqlite:$dbPath");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

// 1) Crear tabla si no existe
$pdo->exec(
    "CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT NOT NULL UNIQUE
    )"
);

echo "Tabla users lista.\n";

// 2) Insertar datos de ejemplo (ignora duplicados por email)
$seedUsers = [
    ["name" => "Ana", "email" => "ana@example.com"],
    ["name" => "Luis", "email" => "luis@example.com"],
];

$insertStmt = $pdo->prepare("INSERT OR IGNORE INTO users (name, email) VALUES (:name, :email)");
foreach ($seedUsers as $user) {
    $insertStmt->execute([
        ":name" => $user["name"],
        ":email" => $user["email"],
    ]);
}

echo "Datos de ejemplo insertados.\n";

// 3) Consultar todos los usuarios
$query = $pdo->query("SELECT id, name, email FROM users ORDER BY id ASC");
$users = $query->fetchAll(PDO::FETCH_ASSOC);

echo "Usuarios actuales:\n";
foreach ($users as $user) {
    echo "- #{$user['id']} {$user['name']} ({$user['email']})\n";
}

// 4) Reto: buscar por email de forma segura
function findUserByEmail(PDO $pdo, string $email): ?array
{
    $stmt = $pdo->prepare("SELECT id, name, email FROM users WHERE email = :email LIMIT 1");
    $stmt->execute([":email" => $email]);
    $result = $stmt->fetch(PDO::FETCH_ASSOC);

    return $result !== false ? $result : null;
}

$found = findUserByEmail($pdo, "ana@example.com");
if ($found) {
    echo "Encontrado: {$found['name']} <{$found['email']}>\n";
} else {
    echo "Usuario no encontrado.\n";
}

echo "=== Fin M3 ===\n";
