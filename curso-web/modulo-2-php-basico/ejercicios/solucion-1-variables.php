<?php
// ============================================================
// SOLUCIÓN EJERCICIO 1: Variables y operaciones básicas
// ============================================================

// PARTE A
$mi_nombre = "Carlos";
$mi_edad = 25;
$mi_estatura = 1.75;

// PARTE B
echo "Me llamo $mi_nombre, tengo $mi_edad años y mido $mi_estatura m\n";

// PARTE C
$precio_unitario = 350.75;
$unidades = 4;
$descuento_porcentaje = 15;

$subtotal = $precio_unitario * $unidades;
$descuento = $subtotal * ($descuento_porcentaje / 100);
$total_con_descuento = $subtotal - $descuento;

echo "\nSubtotal: $" . number_format($subtotal, 2) . "\n";
echo "Descuento ($descuento_porcentaje%): -$" . number_format($descuento, 2) . "\n";
echo "Total: $" . number_format($total_con_descuento, 2) . "\n";

// PARTE D
$ciudad = "Monterrey";
$pais = "México";
$ubicacion = $ciudad . ", " . $pais;
echo "\nUbicación: $ubicacion\n";

// PARTE E
echo "\nNombre en mayúsculas: " . strtoupper($mi_nombre) . "\n";
echo "Caracteres: " . strlen($mi_nombre) . "\n";
