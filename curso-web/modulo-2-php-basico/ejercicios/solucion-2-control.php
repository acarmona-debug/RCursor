<?php
// ============================================================
// SOLUCIÓN EJERCICIO 2: Estructuras de control
// ============================================================

// PARTE A: Clasificador de temperaturas
$temperatura = 22;
echo "=== Clasificador de temperatura ===\n";
if ($temperatura >= 35) {
    echo "{$temperatura}°C → Muy caliente\n";
} elseif ($temperatura >= 25) {
    echo "{$temperatura}°C → Caliente\n";
} elseif ($temperatura >= 15) {
    echo "{$temperatura}°C → Templado\n";
} elseif ($temperatura >= 5) {
    echo "{$temperatura}°C → Frío\n";
} else {
    echo "{$temperatura}°C → Muy frío\n";
}

// PARTE B: Tabla de multiplicar del 7
echo "\n=== Tabla del 7 ===\n";
for ($i = 1; $i <= 10; $i++) {
    echo "7 x $i = " . (7 * $i) . "\n";
}

// PARTE C: Números pares del 2 al 20
echo "\n=== Números pares del 2 al 20 ===\n";
for ($i = 1; $i <= 20; $i++) {
    if ($i % 2 == 0) {
        echo "$i ";
    }
}
echo "\n";

// PARTE D: Lista de compras
$lista_compras = [
    ["item" => "Leche", "precio" => 28.50],
    ["item" => "Pan", "precio" => 35.00],
    ["item" => "Huevos", "precio" => 45.90],
    ["item" => "Cereal", "precio" => 62.00],
    ["item" => "Jugo", "precio" => 23.50],
];

echo "\n=== Lista de compras ===\n";
$total = 0;
foreach ($lista_compras as $compra) {
    echo str_pad($compra["item"], 10) . " $" . number_format($compra["precio"], 2) . "\n";
    $total += $compra["precio"];
}
echo str_repeat("-", 20) . "\n";
echo str_pad("TOTAL:", 10) . " $" . number_format($total, 2) . "\n";

// PARTE E: FizzBuzz
echo "\n=== FizzBuzz ===\n";
for ($i = 1; $i <= 30; $i++) {
    if ($i % 3 == 0 && $i % 5 == 0) {
        echo "FizzBuzz ";
    } elseif ($i % 3 == 0) {
        echo "Fizz ";
    } elseif ($i % 5 == 0) {
        echo "Buzz ";
    } else {
        echo "$i ";
    }
}
echo "\n";
