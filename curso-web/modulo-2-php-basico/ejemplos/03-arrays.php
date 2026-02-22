<?php
// ============================================================
// EJEMPLO: Arrays (listas) en PHP
// Ejecuta con: php 03-arrays.php
// ============================================================

echo "=== Array simple (indexado) ===\n";
$frutas = ["Manzana", "Plátano", "Naranja", "Uva", "Fresa"];

echo "Primera fruta: $frutas[0]\n";
echo "Última fruta: " . end($frutas) . "\n";
echo "Total de frutas: " . count($frutas) . "\n";

echo "\nTodas las frutas:\n";
foreach ($frutas as $i => $fruta) {
    echo "  [$i] $fruta\n";
}

$frutas[] = "Mango";
echo "\nDespués de agregar Mango: " . count($frutas) . " frutas\n";

echo "\n=== Array asociativo ===\n";
$producto = [
    "nombre" => "Laptop HP",
    "precio" => 12500.00,
    "stock" => 10,
    "categoria" => "Electrónica"
];

foreach ($producto as $clave => $valor) {
    echo "  $clave: $valor\n";
}

echo "\n=== Array de arrays (lista de productos) ===\n";
$productos = [
    ["nombre" => "Laptop", "precio" => 12500, "stock" => 10],
    ["nombre" => "Mouse", "precio" => 450, "stock" => 25],
    ["nombre" => "Teclado", "precio" => 1200, "stock" => 15],
    ["nombre" => "Monitor", "precio" => 5600, "stock" => 8],
];

echo str_pad("Producto", 12) . str_pad("Precio", 10) . "Stock\n";
echo str_repeat("-", 32) . "\n";

$total_inventario = 0;
foreach ($productos as $p) {
    echo str_pad($p["nombre"], 12);
    echo str_pad("$" . $p["precio"], 10);
    echo $p["stock"] . "\n";
    $total_inventario += $p["precio"] * $p["stock"];
}

echo str_repeat("-", 32) . "\n";
echo "Valor total del inventario: \$" . number_format($total_inventario, 2) . "\n";
