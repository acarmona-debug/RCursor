<?php
// ============================================================
// EJEMPLO: Funciones en PHP
// Ejecuta con: php 04-funciones.php
// ============================================================

// Función simple
function saludar($nombre) {
    return "¡Hola, $nombre! Bienvenido/a.";
}

echo saludar("Carlos") . "\n";
echo saludar("María") . "\n";

// Función con cálculos
function calcular_factura($precio, $cantidad, $descuento = 0) {
    $subtotal = $precio * $cantidad;
    $monto_descuento = $subtotal * ($descuento / 100);
    $subtotal_con_descuento = $subtotal - $monto_descuento;
    $iva = $subtotal_con_descuento * 0.16;
    $total = $subtotal_con_descuento + $iva;

    return [
        "subtotal" => $subtotal,
        "descuento" => $monto_descuento,
        "iva" => $iva,
        "total" => $total
    ];
}

echo "\n=== Factura ===\n";
$factura = calcular_factura(500, 3, 10);
foreach ($factura as $concepto => $monto) {
    echo str_pad(ucfirst($concepto) . ":", 15) . "$" . number_format($monto, 2) . "\n";
}

// Función que valida un email
function validar_email($email) {
    if (empty($email)) {
        return ["valido" => false, "mensaje" => "El email está vacío"];
    }
    if (!str_contains($email, "@")) {
        return ["valido" => false, "mensaje" => "Falta el símbolo @"];
    }
    if (!str_contains($email, ".")) {
        return ["valido" => false, "mensaje" => "Falta el punto del dominio"];
    }
    return ["valido" => true, "mensaje" => "Email válido"];
}

echo "\n=== Validación de emails ===\n";
$emails = ["carlos@email.com", "invalido", "", "sin-arroba.com", "test@test.mx"];

foreach ($emails as $email) {
    $resultado = validar_email($email);
    $estado = $resultado["valido"] ? "OK" : "ERROR";
    $display = empty($email) ? "(vacío)" : $email;
    echo "[$estado] $display → {$resultado['mensaje']}\n";
}

// Función que busca en un array
function buscar_producto($productos, $nombre) {
    foreach ($productos as $producto) {
        if (strtolower($producto["nombre"]) === strtolower($nombre)) {
            return $producto;
        }
    }
    return null;
}

$catalogo = [
    ["nombre" => "Laptop", "precio" => 12500],
    ["nombre" => "Mouse", "precio" => 450],
    ["nombre" => "Teclado", "precio" => 1200],
];

echo "\n=== Búsqueda de productos ===\n";
$encontrado = buscar_producto($catalogo, "mouse");
if ($encontrado) {
    echo "Encontrado: {$encontrado['nombre']} - \${$encontrado['precio']}\n";
} else {
    echo "Producto no encontrado\n";
}
