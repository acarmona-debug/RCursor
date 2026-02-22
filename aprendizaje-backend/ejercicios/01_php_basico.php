<?php

declare(strict_types=1);

echo "=== M1: PHP basico ===\n";

// 1) Variables y tipos
$name = "TU_NOMBRE";
$age = 18;
$isStudent = true;

echo "Hola, $name\n";
echo "Edad: $age\n";
echo "Es estudiante? " . ($isStudent ? "si" : "no") . "\n";

// 2) Condicional basico
if ($age >= 18) {
    echo "Eres mayor de edad.\n";
} else {
    echo "Eres menor de edad.\n";
}

// 3) Bucle for
echo "Contando del 1 al 5:\n";
for ($i = 1; $i <= 5; $i++) {
    echo "- $i\n";
}

// 4) Tu reto
// Crea una variable $city y muestra:
// "Vives en: <ciudad>"
// Luego crea un if que diga:
// - "Clima calido" si city es "Lima" o "Cartagena"
// - "Clima variable" en cualquier otro caso

// TODO: escribe aqui tu solucion.
$city = "CAMBIAME";
echo "Vives en: $city\n";

if ($city === "Lima" || $city === "Cartagena") {
    echo "Clima calido\n";
} else {
    echo "Clima variable\n";
}

echo "=== Fin M1 ===\n";
