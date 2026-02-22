<?php

declare(strict_types=1);

// Ejecuta: php sandbox/php_basics.php

$list = [1, 2, 3];
$todo = [
    'id' => 1,
    'title' => 'Aprender PHP',
    'done' => false,
];

function greet(string $name): string
{
    return 'Hola, ' . $name;
}

echo greet('mundo') . PHP_EOL;

echo PHP_EOL . 'Lista:' . PHP_EOL;
var_dump($list);

echo PHP_EOL . 'Mapa (todo):' . PHP_EOL;
var_dump($todo);

$json = json_encode($todo, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
echo PHP_EOL . 'JSON:' . PHP_EOL;
echo $json . PHP_EOL;

$decoded = json_decode((string) $json, true);
echo PHP_EOL . 'JSON -> array:' . PHP_EOL;
var_dump($decoded);

