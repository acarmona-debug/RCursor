<?php
// ============================================================
// EJEMPLO: Condicionales (if, elseif, else, switch)
// Ejecuta con: php 02-condicionales.php
// ============================================================

echo "=== Sistema de calificaciones ===\n\n";

$calificaciones = [95, 82, 67, 45, 78, 91, 55, 88];

foreach ($calificaciones as $index => $nota) {
    $numero = $index + 1;

    if ($nota >= 90) {
        $resultado = "Excelente (A)";
    } elseif ($nota >= 80) {
        $resultado = "Muy bien (B)";
    } elseif ($nota >= 70) {
        $resultado = "Bien (C)";
    } elseif ($nota >= 60) {
        $resultado = "Suficiente (D)";
    } else {
        $resultado = "Reprobado (F)";
    }

    $estado = ($nota >= 60) ? "APROBADO" : "REPROBADO";

    echo "Alumno $numero: $nota puntos → $resultado [$estado]\n";
}

echo "\n=== Verificación de edad ===\n\n";

$edades = [15, 18, 21, 12, 30];

foreach ($edades as $edad) {
    if ($edad < 13) {
        echo "Edad $edad: Niño\n";
    } elseif ($edad < 18) {
        echo "Edad $edad: Adolescente\n";
    } elseif ($edad < 65) {
        echo "Edad $edad: Adulto\n";
    } else {
        echo "Edad $edad: Adulto mayor\n";
    }
}
