<?php

declare(strict_types=1);

// Ejecuta: php sandbox/pdo_sqlite.php

require __DIR__ . '/../src/db.php';

$db = db();

$title = 'DB ok: ' . gmdate('c');
$stmt = $db->prepare('INSERT INTO todos (title, done) VALUES (:title, :done)');
$stmt->execute([
    ':title' => $title,
    ':done' => 0,
]);

$stmt = $db->query('SELECT id, title, done, created_at, updated_at FROM todos ORDER BY id DESC LIMIT 5');
$rows = $stmt->fetchAll();

echo "Ultimos 5 todos:\n";
var_dump($rows);

