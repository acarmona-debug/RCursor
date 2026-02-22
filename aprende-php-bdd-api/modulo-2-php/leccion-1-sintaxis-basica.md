# Módulo 2: PHP Básico

## Lección 1: ¿Qué es PHP?

**PHP** = Hypertext Preprocessor

Es un lenguaje que se ejecuta en el **servidor**. Cuando alguien visita tu web, el servidor procesa el PHP y devuelve HTML al navegador.

```
Usuario → pide página → Servidor ejecuta PHP → Devuelve HTML → Usuario ve resultado
```

---

## Lección 2: Sintaxis básica

### Variables
```php
<?php
$nombre = "María";
$edad = 25;
$precio = 19.99;
$activo = true;

echo $nombre;  // Imprime: María
?>
```

- Las variables empiezan con `$`
- No hace falta declarar el tipo
- Punto y coma al final (`;`)

### Concatenar texto
```php
<?php
$nombre = "Carlos";
echo "Hola, " . $nombre;  // Hola, Carlos
echo "Tienes $edad años"; // O con comillas dobles: Tienes 25 años
?>
```

### Arrays (listas)
```php
<?php
$frutas = ["manzana", "naranja", "plátano"];
echo $frutas[0];  // manzana

$persona = [
    "nombre" => "Ana",
    "edad" => 28,
    "ciudad" => "Madrid"
];
echo $persona["nombre"];  // Ana
?>
```

### Condicionales
```php
<?php
$edad = 18;
if ($edad >= 18) {
    echo "Eres mayor de edad";
} elseif ($edad >= 13) {
    echo "Eres adolescente";
} else {
    echo "Eres menor";
}
?>
```

### Bucles
```php
<?php
// for
for ($i = 0; $i < 5; $i++) {
    echo "Número: $i\n";
}

// foreach (para arrays)
$colores = ["rojo", "verde", "azul"];
foreach ($colores as $color) {
    echo $color . "\n";
}
?>
```

---

## Lección 3: Funciones

```php
<?php
function saludar($nombre) {
    return "Hola, " . $nombre;
}

echo saludar("Lucía");  // Hola, Lucía

function sumar($a, $b) {
    return $a + $b;
}
echo sumar(5, 3);  // 8
?>
```

---

## ✏️ Ejercicio 2.1

Crea un script PHP que:
1. Defina un array con 3 nombres
2. Use un `foreach` para imprimir "Hola, [nombre]" para cada uno

**Archivo:** `ejercicio-2-1.php` (crea el archivo y escribe el código)
