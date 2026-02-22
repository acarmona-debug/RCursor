<?php
// ============================================================
// SOLUCIÓN EJERCICIO 3: Funciones
// ============================================================

// FUNCIÓN 1
function calcular_area_rectangulo($base, $altura) {
    return $base * $altura;
}

echo "Área (5x3): " . calcular_area_rectangulo(5, 3) . "\n";
echo "Área (10x7): " . calcular_area_rectangulo(10, 7) . "\n";

// FUNCIÓN 2
function es_mayor_de_edad($edad) {
    return $edad >= 18;
}

echo "\n20 años: " . (es_mayor_de_edad(20) ? "Mayor" : "Menor") . "\n";
echo "15 años: " . (es_mayor_de_edad(15) ? "Mayor" : "Menor") . "\n";

// FUNCIÓN 3
function convertir_celsius_a_fahrenheit($celsius) {
    return ($celsius * 9 / 5) + 32;
}

echo "\n0°C = " . convertir_celsius_a_fahrenheit(0) . "°F\n";
echo "100°C = " . convertir_celsius_a_fahrenheit(100) . "°F\n";
echo "22°C = " . convertir_celsius_a_fahrenheit(22) . "°F\n";

// FUNCIÓN 4
function contar_vocales($texto) {
    $texto = strtolower($texto);
    $vocales = ['a', 'e', 'i', 'o', 'u'];
    $contador = 0;

    $caracteres = str_split($texto);
    foreach ($caracteres as $char) {
        if (in_array($char, $vocales)) {
            $contador++;
        }
    }

    return $contador;
}

echo "\nVocales en 'Hola Mundo': " . contar_vocales("Hola Mundo") . "\n";
echo "Vocales en 'PHP es genial': " . contar_vocales("PHP es genial") . "\n";

// FUNCIÓN 5
function generar_password($longitud = 8) {
    $caracteres = "abcdefghijklmnopqrstuvwxyz0123456789";
    $password = "";

    for ($i = 0; $i < $longitud; $i++) {
        $indice = rand(0, strlen($caracteres) - 1);
        $password .= $caracteres[$indice];
    }

    return $password;
}

echo "\nPassword (8): " . generar_password() . "\n";
echo "Password (12): " . generar_password(12) . "\n";
echo "Password (6): " . generar_password(6) . "\n";

// FUNCIÓN 6
function buscar_mayor($numeros) {
    $mayor = $numeros[0];

    foreach ($numeros as $numero) {
        if ($numero > $mayor) {
            $mayor = $numero;
        }
    }

    return $mayor;
}

echo "\nMayor de [34,78,12,95,43]: " . buscar_mayor([34, 78, 12, 95, 43]) . "\n";
echo "Mayor de [1,2,3]: " . buscar_mayor([1, 2, 3]) . "\n";
