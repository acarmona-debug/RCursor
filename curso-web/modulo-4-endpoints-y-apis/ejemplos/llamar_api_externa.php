<?php
// ============================================================
// EJEMPLO: Llamar a una API externa desde PHP
// ============================================================
// Usa la API pública JSONPlaceholder para practicar
// Ejecuta con: php llamar_api_externa.php
// ============================================================

echo "=== Consumir APIs externas con PHP ===\n\n";

// ----------------------------------------------------------
// 1. GET: Obtener datos de una API
// ----------------------------------------------------------
echo "--- 1. GET: Obtener un post ---\n";

$url = "https://jsonplaceholder.typicode.com/posts/1";
$respuesta = file_get_contents($url);
$post = json_decode($respuesta, true);

echo "Título: {$post['title']}\n";
echo "Cuerpo: " . substr($post['body'], 0, 80) . "...\n";

// ----------------------------------------------------------
// 2. GET: Obtener una lista
// ----------------------------------------------------------
echo "\n--- 2. GET: Obtener lista de usuarios ---\n";

$url = "https://jsonplaceholder.typicode.com/users";
$respuesta = file_get_contents($url);
$usuarios = json_decode($respuesta, true);

echo "Total de usuarios: " . count($usuarios) . "\n";
foreach (array_slice($usuarios, 0, 3) as $u) {
    echo "  - {$u['name']} ({$u['email']})\n";
}
echo "  ... y " . (count($usuarios) - 3) . " más\n";

// ----------------------------------------------------------
// 3. POST: Enviar datos a una API (con cURL)
// ----------------------------------------------------------
echo "\n--- 3. POST: Crear un nuevo post ---\n";

$datos = [
    "title" => "Mi primer post desde PHP",
    "body" => "Este post fue creado usando cURL desde PHP",
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
echo "Código HTTP: $codigo\n";
echo "Post creado con ID: {$resultado['id']}\n";

// ----------------------------------------------------------
// 4. GET con parámetros
// ----------------------------------------------------------
echo "\n--- 4. GET: Posts del usuario 1 ---\n";

$url = "https://jsonplaceholder.typicode.com/posts?userId=1";
$respuesta = file_get_contents($url);
$posts = json_decode($respuesta, true);

echo "El usuario 1 tiene " . count($posts) . " posts\n";
foreach (array_slice($posts, 0, 3) as $p) {
    echo "  - [{$p['id']}] {$p['title']}\n";
}

// ----------------------------------------------------------
// 5. Manejo de errores
// ----------------------------------------------------------
echo "\n--- 5. Manejo de errores ---\n";

$ch = curl_init("https://jsonplaceholder.typicode.com/posts/99999");
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$respuesta = curl_exec($ch);
$codigo = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "Código HTTP: $codigo\n";
if ($codigo == 200) {
    $datos = json_decode($respuesta, true);
    if (empty($datos)) {
        echo "El post no existe (respuesta vacía)\n";
    } else {
        echo "Post encontrado\n";
    }
} elseif ($codigo == 404) {
    echo "Post no encontrado (404)\n";
} else {
    echo "Error inesperado\n";
}

echo "\n¡Listo! Ahora sabes consumir APIs externas.\n";
