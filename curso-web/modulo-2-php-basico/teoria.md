# Módulo 2: PHP desde Cero

## 2.1 ¿Qué es PHP?

**PHP** es un lenguaje de programación que se ejecuta en el **servidor**.
Cuando visitas una página web, tu navegador (el **cliente**) le pide información
a un **servidor**. PHP es el que procesa esa petición en el servidor y genera
la respuesta.

### ¿Por qué PHP?

- Es el lenguaje más usado para desarrollo web del lado del servidor
- Facebook, Wikipedia y WordPress están hechos con PHP
- Es fácil de aprender
- Funciona muy bien con bases de datos (MySQL)

---

## 2.2 Tu primer archivo PHP

Todo archivo PHP tiene extensión `.php` y el código va entre las etiquetas `<?php` y `?>`.

```php
<?php
echo "¡Hola mundo!";
?>
```

- `echo` es como decir "muestra esto en pantalla"
- Cada instrucción termina con punto y coma `;`

### ¿Cómo ejecutar PHP?

Desde la terminal:
```bash
php archivo.php
```

---

## 2.3 Variables

Una **variable** es una caja donde guardas un dato. En PHP, las variables
siempre empiezan con el signo `$`.

```php
<?php
$nombre = "Carlos";        // Texto (string)
$edad = 25;                // Número entero (integer)
$precio = 99.99;           // Número decimal (float)
$activo = true;            // Verdadero/falso (boolean)

echo $nombre;              // Muestra: Carlos
echo "Tengo $edad años";   // Muestra: Tengo 25 años
?>
```

### Reglas de variables:
- Siempre empiezan con `$`
- El nombre puede tener letras, números y guión bajo `_`
- No pueden empezar con número: `$1nombre` es inválido
- Son sensibles a mayúsculas: `$nombre` y `$Nombre` son diferentes

---

## 2.4 Tipos de datos

| Tipo     | Ejemplo              | Descripción               |
|----------|----------------------|---------------------------|
| string   | `"Hola"`, `'Mundo'`  | Texto                     |
| int      | `42`, `-5`           | Número entero             |
| float    | `3.14`, `99.99`      | Número decimal            |
| bool     | `true`, `false`      | Verdadero o falso         |
| array    | `[1, 2, 3]`          | Lista de valores          |
| null     | `null`               | Sin valor / vacío         |

---

## 2.5 Operadores

### Aritméticos (matemáticas)
```php
<?php
$a = 10;
$b = 3;

echo $a + $b;   // 13  (suma)
echo $a - $b;   // 7   (resta)
echo $a * $b;   // 30  (multiplicación)
echo $a / $b;   // 3.33 (división)
echo $a % $b;   // 1   (residuo/módulo)
?>
```

### De comparación
```php
<?php
$a == $b    // ¿Son iguales en valor?
$a === $b   // ¿Son iguales en valor Y tipo?
$a != $b    // ¿Son diferentes?
$a > $b     // ¿a es mayor que b?
$a < $b     // ¿a es menor que b?
$a >= $b    // ¿a es mayor o igual?
$a <= $b    // ¿a es menor o igual?
?>
```

### De concatenación (unir textos)
```php
<?php
$nombre = "Carlos";
$apellido = "López";

// Con punto (.)
$completo = $nombre . " " . $apellido;
echo $completo; // Carlos López

// Con comillas dobles (interpola variables)
echo "$nombre $apellido"; // Carlos López
?>
```

---

## 2.6 Estructuras de control

### if / else (condiciones)
```php
<?php
$edad = 18;

if ($edad >= 18) {
    echo "Eres mayor de edad";
} else {
    echo "Eres menor de edad";
}

// También puedes encadenar condiciones con elseif
$nota = 85;

if ($nota >= 90) {
    echo "Excelente";
} elseif ($nota >= 70) {
    echo "Aprobado";
} else {
    echo "Reprobado";
}
?>
```

### while (repetir mientras se cumpla condición)
```php
<?php
$contador = 1;

while ($contador <= 5) {
    echo "Vuelta número: $contador\n";
    $contador++;
}
// Muestra: Vuelta número: 1, 2, 3, 4, 5
?>
```

### for (repetir un número específico de veces)
```php
<?php
for ($i = 1; $i <= 10; $i++) {
    echo "$i x 5 = " . ($i * 5) . "\n";
}
// Muestra la tabla del 5
?>
```

### foreach (recorrer arrays)
```php
<?php
$frutas = ["Manzana", "Plátano", "Naranja", "Uva"];

foreach ($frutas as $fruta) {
    echo "Fruta: $fruta\n";
}
?>
```

---

## 2.7 Arrays (listas)

Un **array** es una lista que guarda múltiples valores.

### Array simple (indexado)
```php
<?php
$colores = ["Rojo", "Azul", "Verde"];

echo $colores[0]; // Rojo  (los índices empiezan en 0)
echo $colores[1]; // Azul
echo $colores[2]; // Verde

$colores[] = "Amarillo"; // Agregar al final
echo count($colores);    // 4 (cuenta los elementos)
?>
```

### Array asociativo (como un diccionario)
```php
<?php
$persona = [
    "nombre" => "María",
    "edad" => 30,
    "email" => "maria@email.com"
];

echo $persona["nombre"]; // María
echo $persona["edad"];   // 30

// Recorrer un array asociativo
foreach ($persona as $clave => $valor) {
    echo "$clave: $valor\n";
}
?>
```

---

## 2.8 Funciones

Una **función** es un bloque de código reutilizable. La defines una vez y
la llamas cuantas veces quieras.

```php
<?php
// Definir una función
function saludar($nombre) {
    return "¡Hola, $nombre!";
}

// Llamar la función
echo saludar("Carlos"); // ¡Hola, Carlos!
echo saludar("María");  // ¡Hola, María!

// Función con varios parámetros
function calcular_total($precio, $cantidad) {
    $subtotal = $precio * $cantidad;
    $iva = $subtotal * 0.16;
    return $subtotal + $iva;
}

echo calcular_total(100, 3); // 348

// Función con valor por defecto
function presentar($nombre, $pais = "México") {
    return "$nombre es de $pais";
}

echo presentar("Carlos");           // Carlos es de México
echo presentar("John", "USA");      // John es de USA
?>
```

---

## 2.9 Funciones útiles integradas

```php
<?php
// STRINGS (texto)
strlen("Hola");           // 4 (longitud)
strtoupper("hola");       // "HOLA" (mayúsculas)
strtolower("HOLA");       // "hola" (minúsculas)
trim("  hola  ");         // "hola" (quitar espacios)
str_replace("a", "o", "casa"); // "coso" (reemplazar)
substr("Hola mundo", 0, 4);   // "Hola" (subcadena)

// ARRAYS
count([1,2,3]);                  // 3 (contar)
array_push($arr, "nuevo");      // agregar al final
in_array("Rojo", $colores);     // true/false (buscar)
sort($arr);                      // ordenar
array_merge($arr1, $arr2);      // combinar arrays

// NÚMEROS
round(3.7);     // 4 (redondear)
ceil(3.2);      // 4 (redondear arriba)
floor(3.9);     // 3 (redondear abajo)
rand(1, 100);   // número aleatorio entre 1 y 100

// FECHA
date("Y-m-d");           // "2026-02-22" (fecha actual)
date("H:i:s");           // "10:30:45" (hora actual)
date("d/m/Y H:i");       // "22/02/2026 10:30"
?>
```

---

## 2.10 Manejo de errores básico

```php
<?php
// Verificar si una variable existe
if (isset($variable)) {
    echo "La variable existe";
}

// Verificar si está vacía
if (empty($nombre)) {
    echo "El nombre está vacío";
}

// Validar tipo de dato
if (is_numeric($edad)) {
    echo "Es un número";
}

if (is_string($nombre)) {
    echo "Es un texto";
}
?>
```

---

## Resumen del Módulo 2

| Concepto          | Qué es                                          |
|-------------------|--------------------------------------------------|
| Variable          | Caja que guarda un dato (`$nombre = "Juan"`)     |
| String            | Texto entre comillas                             |
| Array             | Lista de valores                                 |
| Array asociativo  | Lista con claves personalizadas                  |
| if/else           | Tomar decisiones                                 |
| for/while         | Repetir acciones                                 |
| foreach           | Recorrer arrays                                  |
| Función           | Bloque de código reutilizable                    |
| echo              | Mostrar algo en pantalla                         |

---

**Siguiente paso:** Ve a la carpeta `ejercicios/` para practicar.
