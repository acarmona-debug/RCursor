<?php

declare(strict_types=1);

echo "=== M2: Funciones y arrays ===\n";

/**
 * Aplica descuento porcentual a un precio.
 */
function applyDiscount(float $price, float $percent): float
{
    if ($percent < 0 || $percent > 100) {
        throw new InvalidArgumentException("El porcentaje debe estar entre 0 y 100");
    }

    $discountAmount = ($price * $percent) / 100;
    return $price - $discountAmount;
}

$products = [
    ["name" => "Teclado", "price" => 35.0, "discount" => 10],
    ["name" => "Mouse", "price" => 20.0, "discount" => 0],
    ["name" => "Monitor", "price" => 150.0, "discount" => 15],
];

foreach ($products as $product) {
    $finalPrice = applyDiscount((float) $product["price"], (float) $product["discount"]);
    echo $product["name"] . " -> $" . number_format($finalPrice, 2) . "\n";
}

// Reto 1:
// Crea una funcion llamada isExpensive(float $price): bool
// Regla: es caro si price > 100
// Luego imprime para cada producto:
// "<nombre>: caro" o "<nombre>: accesible"

function isExpensive(float $price): bool
{
    return $price > 100;
}

foreach ($products as $product) {
    $label = isExpensive((float) $product["price"]) ? "caro" : "accesible";
    echo $product["name"] . ": $label\n";
}

// Reto 2:
// Construye un array solo con nombres de productos con descuento > 0
// Pista: foreach + if

$discountedNames = [];
foreach ($products as $product) {
    if ((float) $product["discount"] > 0) {
        $discountedNames[] = $product["name"];
    }
}

echo "Productos con descuento: " . implode(", ", $discountedNames) . "\n";

echo "=== Fin M2 ===\n";
