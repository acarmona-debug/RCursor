<?php
// ============================================================
// Archivo de conexión reutilizable
// Incluye este archivo en cualquier script que necesite BD
// ============================================================

function obtener_conexion() {
    $host = "localhost";
    $dbname = "tienda";
    $usuario = "root";
    $password = "";

    try {
        $conexion = new PDO(
            "mysql:host=$host;dbname=$dbname;charset=utf8",
            $usuario,
            $password
        );
        $conexion->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
        $conexion->setAttribute(PDO::ATTR_DEFAULT_FETCH_MODE, PDO::FETCH_ASSOC);
        return $conexion;
    } catch (PDOException $e) {
        die("Error de conexión: " . $e->getMessage() . "\n");
    }
}
