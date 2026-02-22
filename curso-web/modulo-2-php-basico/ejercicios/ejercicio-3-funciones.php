<?php
// ============================================================
// EJERCICIO 3: Funciones
// ============================================================
// Ejecuta con: php ejercicio-3-funciones.php
// ============================================================

// FUNCIÓN 1: calcular_area_rectangulo($base, $altura)
// Recibe base y altura, devuelve el área (base * altura)
// TU CÓDIGO AQUÍ:



// Prueba:
// echo calcular_area_rectangulo(5, 3);  // Debe mostrar 15

// FUNCIÓN 2: es_mayor_de_edad($edad)
// Recibe una edad, devuelve true si >= 18, false si no
// TU CÓDIGO AQUÍ:



// Prueba:
// echo es_mayor_de_edad(20) ? "Mayor" : "Menor";  // Mayor
// echo es_mayor_de_edad(15) ? "Mayor" : "Menor";  // Menor

// FUNCIÓN 3: convertir_celsius_a_fahrenheit($celsius)
// Fórmula: F = (C * 9/5) + 32
// TU CÓDIGO AQUÍ:



// Prueba:
// echo convertir_celsius_a_fahrenheit(0);    // 32
// echo convertir_celsius_a_fahrenheit(100);  // 212

// FUNCIÓN 4: contar_vocales($texto)
// Recibe un texto y devuelve cuántas vocales tiene (a,e,i,o,u)
// Pista: convierte a minúsculas primero con strtolower()
// Pista: usa str_split() para convertir string a array de caracteres
// TU CÓDIGO AQUÍ:



// Prueba:
// echo contar_vocales("Hola Mundo");  // Debe mostrar 4

// FUNCIÓN 5: generar_password($longitud = 8)
// Genera una contraseña aleatoria de la longitud indicada
// Usa estos caracteres: abcdefghijklmnopqrstuvwxyz0123456789
// Pista: usa strlen() y rand() para elegir caracteres aleatorios
// TU CÓDIGO AQUÍ:



// Prueba:
// echo generar_password();    // algo como "k3m9x2p1"
// echo generar_password(12);  // algo como "a8b3c7d1e5f2"

// FUNCIÓN 6: buscar_mayor($numeros)
// Recibe un array de números y devuelve el mayor
// NO uses la función max() de PHP, hazlo manualmente con un ciclo
// TU CÓDIGO AQUÍ:



// Prueba:
// echo buscar_mayor([34, 78, 12, 95, 43]);  // 95

