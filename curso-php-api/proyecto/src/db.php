<?php

declare(strict_types=1);

function db_path(): string
{
    return __DIR__ . '/../storage/database.sqlite';
}

function db(): PDO
{
    static $pdo = null;
    if ($pdo instanceof PDO) {
        return $pdo;
    }

    $path = db_path();
    $dir = dirname($path);
    if (!is_dir($dir)) {
        mkdir($dir, 0777, true);
    }

    $pdo = new PDO('sqlite:' . $path, null, null, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES => false,
    ]);

    $pdo->exec('PRAGMA foreign_keys = ON;');
    init_schema($pdo);

    return $pdo;
}

function init_schema(PDO $db): void
{
    $db->exec(
        'CREATE TABLE IF NOT EXISTS todos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            done INTEGER NOT NULL DEFAULT 0 CHECK (done IN (0, 1)),
            created_at TEXT NOT NULL DEFAULT (datetime(\'now\')),
            updated_at TEXT NOT NULL DEFAULT (datetime(\'now\'))
        )'
    );

    $db->exec(
        'CREATE TRIGGER IF NOT EXISTS todos_set_updated_at
        AFTER UPDATE ON todos
        FOR EACH ROW
        BEGIN
            UPDATE todos SET updated_at = datetime(\'now\') WHERE id = OLD.id;
        END'
    );
}

