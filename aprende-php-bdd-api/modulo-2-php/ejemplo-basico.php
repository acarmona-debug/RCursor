<?php
// Ejemplo básico de PHP - Ejecuta con: php ejemplo-basico.php

$nombre = "Estudiante";
$edad = 20;

echo "=== Ejemplo PHP Básico ===\n\n";
echo "Hola, $nombre\n";
echo "Tu edad es: $edad\n\n";

// Array
$hobbies = ["programar", "leer", "música"];
echo "Tus hobbies:\n";
foreach ($hobbies as $hobby) {
    echo "  - $hobby\n";
}

echo "\n¡PHP funciona correctamente!\n";
?>
