<?php
// ============================================================
// EJERCICIO 1: CRUD completo de clientes
// ============================================================
// Implementa las funciones para gestionar la tabla "clientes"
// Prerequisito: haber ejecutado setup_database.sql
// ============================================================

require_once '../ejemplos/conexion.php';

$db = obtener_conexion();

// FUNCIÓN 1: Crear un nuevo cliente
// Parámetros: $db, $nombre, $email, $ciudad
// Debe insertar el cliente y devolver su ID
// TU CÓDIGO AQUÍ:
function crear_cliente($db, $nombre, $email, $ciudad) {
    // Implementa aquí...
}


// FUNCIÓN 2: Obtener todos los clientes
// Parámetros: $db
// Debe devolver un array con todos los clientes
// TU CÓDIGO AQUÍ:
function obtener_clientes($db) {
    // Implementa aquí...
}


// FUNCIÓN 3: Buscar cliente por email
// Parámetros: $db, $email
// Debe devolver el cliente encontrado o null
// TU CÓDIGO AQUÍ:
function buscar_por_email($db, $email) {
    // Implementa aquí...
}


// FUNCIÓN 4: Actualizar email de un cliente
// Parámetros: $db, $id, $nuevo_email
// Debe actualizar el email y devolver cuántas filas se afectaron
// TU CÓDIGO AQUÍ:
function actualizar_email($db, $id, $nuevo_email) {
    // Implementa aquí...
}


// FUNCIÓN 5: Eliminar un cliente
// Parámetros: $db, $id
// Debe eliminar el cliente y devolver cuántas filas se afectaron
// TU CÓDIGO AQUÍ:
function eliminar_cliente($db, $id) {
    // Implementa aquí...
}


// FUNCIÓN 6: Contar clientes por ciudad
// Parámetros: $db, $ciudad
// Debe devolver el número de clientes en esa ciudad
// TU CÓDIGO AQUÍ:
function contar_por_ciudad($db, $ciudad) {
    // Implementa aquí...
}


// ============================================================
// PRUEBAS - Descomenta para probar tus funciones
// ============================================================

// echo "--- Crear cliente ---\n";
// $id = crear_cliente($db, "Test User", "test@email.com", "CDMX");
// echo "Cliente creado con ID: $id\n";

// echo "\n--- Todos los clientes ---\n";
// $clientes = obtener_clientes($db);
// foreach ($clientes as $c) {
//     echo "  [{$c['id']}] {$c['nombre']} - {$c['email']} ({$c['ciudad']})\n";
// }

// echo "\n--- Buscar por email ---\n";
// $cliente = buscar_por_email($db, "test@email.com");
// echo $cliente ? "Encontrado: {$cliente['nombre']}\n" : "No encontrado\n";

// echo "\n--- Actualizar email ---\n";
// $filas = actualizar_email($db, $id, "nuevo@email.com");
// echo "Filas afectadas: $filas\n";

// echo "\n--- Contar por ciudad ---\n";
// echo "Clientes en CDMX: " . contar_por_ciudad($db, "CDMX") . "\n";

// echo "\n--- Eliminar cliente ---\n";
// $filas = eliminar_cliente($db, $id);
// echo "Eliminados: $filas\n";
