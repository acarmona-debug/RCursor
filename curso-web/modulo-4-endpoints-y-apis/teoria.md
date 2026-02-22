# Módulo 4: Endpoints y APIs

## 4.1 ¿Qué es una API?

**API** = Application Programming Interface (Interfaz de Programación de Aplicaciones)

Una API es como un **mesero en un restaurante**:
- Tú (el cliente) haces un **pedido** (petición)
- El mesero (la API) lleva tu pedido a la **cocina** (servidor/base de datos)
- La cocina prepara la comida y el mesero te la **trae** (respuesta)

En el mundo web:
```
[App/Navegador]  →  petición  →  [API/Servidor]  →  [Base de datos]
                 ←  respuesta ←
```

### ¿Para qué sirven las APIs?

- Una app móvil necesita datos del servidor → usa una API
- Una página web necesita cargar productos → usa una API
- Dos sistemas necesitan comunicarse → usan APIs

---

## 4.2 ¿Qué es HTTP?

**HTTP** (HyperText Transfer Protocol) es el "idioma" que usan los navegadores
y servidores para comunicarse. Cada vez que visitas una página web, tu
navegador hace una petición HTTP.

### Partes de una petición HTTP

```
[MÉTODO] [URL]
[Headers/Cabeceras]
[Body/Cuerpo] (opcional)
```

Ejemplo real:
```
GET /api/productos HTTP/1.1
Host: mi-tienda.com
Accept: application/json
```

---

## 4.3 Métodos HTTP (los más importantes)

| Método   | Para qué se usa          | Equivalente CRUD | Ejemplo                    |
|----------|--------------------------|-------------------|----------------------------|
| **GET**  | Obtener/leer datos       | READ              | Ver lista de productos     |
| **POST** | Crear datos nuevos       | CREATE            | Agregar un producto        |
| **PUT**  | Actualizar datos (todo)  | UPDATE            | Editar un producto entero  |
| **PATCH**| Actualizar datos (parte) | UPDATE            | Cambiar solo el precio     |
| **DELETE**| Borrar datos            | DELETE            | Eliminar un producto       |

### Analogía del correo:
- **GET** = "Muéstrame la carta" (solo lees, no cambias nada)
- **POST** = "Aquí tienes un paquete nuevo" (envías algo nuevo)
- **PUT** = "Reemplaza este paquete por este otro" (cambias todo)
- **DELETE** = "Tira este paquete a la basura" (borras)

---

## 4.4 ¿Qué es un Endpoint?

Un **endpoint** es una URL específica donde la API recibe peticiones.
Es como la dirección exacta a la que envías tu pedido.

### Ejemplos de endpoints para una tienda:

| Método | Endpoint              | Qué hace                     |
|--------|-----------------------|------------------------------|
| GET    | /api/productos        | Lista todos los productos    |
| GET    | /api/productos/1      | Muestra el producto con id=1 |
| POST   | /api/productos        | Crea un producto nuevo       |
| PUT    | /api/productos/1      | Actualiza el producto id=1   |
| DELETE | /api/productos/1      | Elimina el producto id=1     |

---

## 4.5 ¿Qué es JSON?

**JSON** (JavaScript Object Notation) es el formato que usan las APIs
para enviar y recibir datos. Es como un "idioma universal" que todos entienden.

```json
{
    "id": 1,
    "nombre": "Laptop HP",
    "precio": 12500.00,
    "en_stock": true,
    "categorias": ["Electrónica", "Computadoras"]
}
```

### JSON en PHP

```php
<?php
// Convertir array de PHP a JSON
$producto = [
    "id" => 1,
    "nombre" => "Laptop HP",
    "precio" => 12500.00
];
$json = json_encode($producto);
echo $json;
// {"id":1,"nombre":"Laptop HP","precio":12500}

// Convertir JSON a array de PHP
$json_texto = '{"nombre":"Mouse","precio":450}';
$datos = json_decode($json_texto, true);
echo $datos["nombre"]; // Mouse
?>
```

---

## 4.6 Códigos de estado HTTP

Cada respuesta de una API incluye un **código de estado** que indica si
todo salió bien o si hubo un error.

### Los más importantes:

| Código | Nombre                | Significado                        |
|--------|-----------------------|------------------------------------|
| **200**| OK                    | Todo bien, aquí están los datos    |
| **201**| Created               | Se creó el recurso exitosamente    |
| **400**| Bad Request           | La petición tiene errores          |
| **401**| Unauthorized          | No tienes permiso (no logueado)    |
| **403**| Forbidden             | No tienes acceso                   |
| **404**| Not Found             | No se encontró lo que buscas       |
| **500**| Internal Server Error | Error en el servidor               |

### Truco para recordarlos:
- **2xx** = Todo bien
- **4xx** = Error del cliente (tú la regaste)
- **5xx** = Error del servidor (ellos la regaron)

---

## 4.7 Crear un endpoint en PHP

### Endpoint básico: Listar productos (GET)

```php
<?php
// archivo: api/productos.php

header('Content-Type: application/json');

$productos = [
    ["id" => 1, "nombre" => "Laptop", "precio" => 12500],
    ["id" => 2, "nombre" => "Mouse", "precio" => 450],
    ["id" => 3, "nombre" => "Teclado", "precio" => 1200],
];

http_response_code(200);
echo json_encode([
    "status" => "success",
    "data" => $productos
]);
?>
```

### Explicación:
- `header('Content-Type: application/json')` → Le dice al cliente: "te respondo en JSON"
- `http_response_code(200)` → Código de estado 200 (todo bien)
- `json_encode(...)` → Convierte el array PHP a formato JSON

---

## 4.8 Detectar el método HTTP

En un endpoint real, necesitas saber SI el cliente quiere leer, crear,
actualizar o borrar. Eso lo detectas con `$_SERVER['REQUEST_METHOD']`.

```php
<?php
header('Content-Type: application/json');

$metodo = $_SERVER['REQUEST_METHOD'];

switch ($metodo) {
    case 'GET':
        // El cliente quiere LEER datos
        echo json_encode(["accion" => "Leyendo datos..."]);
        break;

    case 'POST':
        // El cliente quiere CREAR datos
        echo json_encode(["accion" => "Creando datos..."]);
        break;

    case 'PUT':
        // El cliente quiere ACTUALIZAR datos
        echo json_encode(["accion" => "Actualizando datos..."]);
        break;

    case 'DELETE':
        // El cliente quiere BORRAR datos
        echo json_encode(["accion" => "Borrando datos..."]);
        break;

    default:
        http_response_code(405);
        echo json_encode(["error" => "Método no permitido"]);
}
?>
```

---

## 4.9 Recibir datos del cliente

### Datos en la URL (GET parameters)

```
GET /api/productos.php?categoria=electronica&precio_max=5000
```

```php
<?php
$categoria = $_GET['categoria'] ?? null;
$precio_max = $_GET['precio_max'] ?? null;

echo "Categoría: $categoria\n";    // electronica
echo "Precio máx: $precio_max\n";  // 5000
?>
```

### Datos en el cuerpo (POST/PUT - JSON)

```php
<?php
$json = file_get_contents('php://input');
$datos = json_decode($json, true);

$nombre = $datos['nombre'] ?? null;
$precio = $datos['precio'] ?? null;

echo "Producto: $nombre, Precio: $precio\n";
?>
```

- `php://input` → Lee el cuerpo de la petición HTTP
- `json_decode($json, true)` → Convierte el JSON a array PHP
- `??` → Operador null coalescing: si no existe, usa el valor por defecto

---

## 4.10 API completa: estructura de respuesta

Una API bien hecha siempre responde con una estructura consistente:

### Respuesta exitosa:
```json
{
    "status": "success",
    "data": {
        "id": 1,
        "nombre": "Laptop HP",
        "precio": 12500
    }
}
```

### Respuesta con error:
```json
{
    "status": "error",
    "message": "Producto no encontrado"
}
```

### Función helper para respuestas:
```php
<?php
function responder($datos, $codigo = 200) {
    http_response_code($codigo);
    echo json_encode([
        "status" => ($codigo >= 200 && $codigo < 300) ? "success" : "error",
        "data" => $datos
    ]);
    exit;
}

function responder_error($mensaje, $codigo = 400) {
    http_response_code($codigo);
    echo json_encode([
        "status" => "error",
        "message" => $mensaje
    ]);
    exit;
}
?>
```

---

## 4.11 Hacer llamadas a APIs desde PHP (cURL)

Además de CREAR endpoints, puedes CONSUMIR APIs externas desde PHP.

```php
<?php
// Llamar a una API externa usando cURL
$url = "https://jsonplaceholder.typicode.com/posts/1";

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
$respuesta = curl_exec($ch);
$codigo = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

if ($codigo == 200) {
    $datos = json_decode($respuesta, true);
    echo "Título: " . $datos['title'] . "\n";
} else {
    echo "Error: código $codigo\n";
}
?>
```

### Hacer un POST a una API externa
```php
<?php
$url = "https://jsonplaceholder.typicode.com/posts";

$datos = [
    "title" => "Mi post",
    "body" => "Contenido del post",
    "userId" => 1
];

$ch = curl_init($url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($datos));
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Content-Type: application/json'
]);

$respuesta = curl_exec($ch);
$codigo = curl_getinfo($ch, CURLINFO_HTTP_CODE);
curl_close($ch);

echo "Código: $codigo\n";
echo "Respuesta: $respuesta\n";
?>
```

---

## 4.12 Probar APIs con herramientas

### Desde la terminal con cURL:
```bash
# GET - Obtener datos
curl http://localhost:8000/api/productos.php

# POST - Crear datos
curl -X POST http://localhost:8000/api/productos.php \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Webcam","precio":650}'

# PUT - Actualizar datos
curl -X PUT http://localhost:8000/api/productos.php?id=1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Webcam HD","precio":750}'

# DELETE - Borrar datos
curl -X DELETE http://localhost:8000/api/productos.php?id=1
```

### Iniciar servidor PHP de desarrollo:
```bash
php -S localhost:8000
```
Esto inicia un servidor web en tu computadora para pruebas.

---

## Resumen del Módulo 4

| Concepto         | Qué es                                             |
|------------------|-----------------------------------------------------|
| API              | Interfaz para que sistemas se comuniquen            |
| HTTP             | Protocolo de comunicación web                       |
| Endpoint         | URL específica que recibe peticiones                |
| GET/POST/PUT/DELETE | Métodos HTTP para CRUD                           |
| JSON             | Formato de datos universal para APIs                |
| Código de estado | Número que indica éxito o error (200, 404, 500...)  |
| cURL             | Herramienta para hacer llamadas HTTP                |
| php://input      | Lee los datos enviados en el cuerpo de la petición  |

---

**Siguiente paso:** Ve a `ejercicios/` para crear tus propios endpoints.
