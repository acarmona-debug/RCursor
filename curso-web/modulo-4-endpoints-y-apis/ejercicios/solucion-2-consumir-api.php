<?php
// ============================================================
// SOLUCIÓN EJERCICIO 2: Consumir una API externa
// ============================================================

// PARTE A: Primeros 5 posts
echo "--- Parte A: Primeros 5 posts ---\n";
$respuesta = file_get_contents("https://jsonplaceholder.typicode.com/posts");
$posts = json_decode($respuesta, true);

foreach (array_slice($posts, 0, 5) as $post) {
    echo "  [{$post['id']}] {$post['title']}\n";
}

// PARTE B: Info del usuario 3
echo "\n--- Parte B: Info del usuario 3 ---\n";
$respuesta = file_get_contents("https://jsonplaceholder.typicode.com/users/3");
$usuario = json_decode($respuesta, true);

echo "  Nombre: {$usuario['name']}\n";
echo "  Email: {$usuario['email']}\n";
echo "  Ciudad: {$usuario['address']['city']}\n";

// PARTE C: Crear un post con cURL
echo "\n--- Parte C: Crear post ---\n";
$datos = [
    "title" => "Aprendiendo APIs con PHP",
    "body" => "Este post fue creado desde un ejercicio del curso",
    "userId" => 1
];

$ch = curl_init("https://jsonplaceholder.typicode.com/posts");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($datos));
curl_setopt($ch, CURLOPT_HTTPHEADER, ['Content-Type: application/json']);

$respuesta = curl_exec($ch);
$codigo = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

$resultado = json_decode($respuesta, true);
echo "  Código HTTP: $codigo\n";
echo "  Post creado con ID: {$resultado['id']}\n";
echo "  Título: {$resultado['title']}\n";

// PARTE D: Comentarios del post 1
echo "\n--- Parte D: Comentarios del post 1 ---\n";
$respuesta = file_get_contents("https://jsonplaceholder.typicode.com/posts/1/comments");
$comentarios = json_decode($respuesta, true);

foreach ($comentarios as $c) {
    echo "  {$c['name']} ({$c['email']})\n";
}

// PARTE E: Función reutilizable
echo "\n--- Parte E: Función reutilizable ---\n";

function llamar_api($url) {
    $ch = curl_init($url);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);

    $respuesta = curl_exec($ch);
    $codigo = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    if ($codigo >= 200 && $codigo < 300) {
        return json_decode($respuesta, true);
    }

    return null;
}

$todos = llamar_api("https://jsonplaceholder.typicode.com/todos?userId=1&completed=true");
if ($todos) {
    echo "  Tareas completadas del usuario 1: " . count($todos) . "\n";
} else {
    echo "  Error al obtener datos\n";
}

$usuario = llamar_api("https://jsonplaceholder.typicode.com/users/1");
if ($usuario) {
    echo "  Usuario 1: {$usuario['name']}\n";
}
