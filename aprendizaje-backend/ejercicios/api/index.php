<?php

declare(strict_types=1);

header("Content-Type: application/json");

$dbPath = __DIR__ . "/api.db";
$pdo = new PDO("sqlite:$dbPath");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

$pdo->exec(
    "CREATE TABLE IF NOT EXISTS users (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        email TEXT NOT NULL UNIQUE
    )"
);

$method = $_SERVER["REQUEST_METHOD"] ?? "GET";
$uri = $_SERVER["REQUEST_URI"] ?? "/";
$path = parse_url($uri, PHP_URL_PATH) ?? "/";

if ($method === "GET" && $path === "/health") {
    respond(200, ["ok" => true, "message" => "API running"]);
}

if ($method === "GET" && $path === "/users") {
    $stmt = $pdo->query("SELECT id, name, email FROM users ORDER BY id ASC");
    $users = $stmt->fetchAll(PDO::FETCH_ASSOC);
    respond(200, ["data" => $users]);
}

if ($method === "POST" && $path === "/users") {
    $raw = file_get_contents("php://input");
    $payload = json_decode($raw, true);

    if (!is_array($payload)) {
        respond(400, ["error" => "Body JSON invalido"]);
    }

    $name = trim((string) ($payload["name"] ?? ""));
    $email = trim((string) ($payload["email"] ?? ""));

    if ($name === "" || $email === "") {
        respond(400, ["error" => "Campos name y email son obligatorios"]);
    }

    try {
        $stmt = $pdo->prepare("INSERT INTO users (name, email) VALUES (:name, :email)");
        $stmt->execute([
            ":name" => $name,
            ":email" => $email,
        ]);

        respond(201, [
            "message" => "Usuario creado",
            "data" => [
                "id" => (int) $pdo->lastInsertId(),
                "name" => $name,
                "email" => $email,
            ],
        ]);
    } catch (PDOException $exception) {
        if ((int) $exception->getCode() === 23000) {
            respond(409, ["error" => "El email ya existe"]);
        }

        respond(500, ["error" => "Error interno al crear usuario"]);
    }
}

respond(404, ["error" => "Endpoint no encontrado"]);

function respond(int $statusCode, array $payload): void
{
    http_response_code($statusCode);
    echo json_encode($payload, JSON_PRETTY_PRINT | JSON_UNESCAPED_SLASHES);
    exit;
}
