<?php
// ============================================================
// EJEMPLO: Variables y tipos de datos en PHP
// Ejecuta con: php 01-variables.php
// ============================================================

// Variables de diferentes tipos
$nombre = "Ana García";
$edad = 28;
$estatura = 1.65;
$es_estudiante = true;

// Mostrar valores
echo "=== Datos Personales ===\n";
echo "Nombre: $nombre\n";
echo "Edad: $edad años\n";
echo "Estatura: $estatura m\n";
echo "¿Es estudiante?: " . ($es_estudiante ? "Sí" : "No") . "\n";

echo "\n=== Tipos de datos ===\n";
echo "nombre es: " . gettype($nombre) . "\n";
echo "edad es: " . gettype($edad) . "\n";
echo "estatura es: " . gettype($estatura) . "\n";
echo "es_estudiante es: " . gettype($es_estudiante) . "\n";

// Operaciones con strings
echo "\n=== Operaciones con texto ===\n";
$nombre_upper = strtoupper($nombre);
echo "Mayúsculas: $nombre_upper\n";
echo "Longitud del nombre: " . strlen($nombre) . " caracteres\n";

// Operaciones matemáticas
echo "\n=== Operaciones matemáticas ===\n";
$precio = 250.50;
$cantidad = 3;
$subtotal = $precio * $cantidad;
$iva = $subtotal * 0.16;
$total = $subtotal + $iva;

echo "Precio unitario: \$$precio\n";
echo "Cantidad: $cantidad\n";
echo "Subtotal: \$$subtotal\n";
echo "IVA (16%): \$$iva\n";
echo "Total: \$$total\n";
