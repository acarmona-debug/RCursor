# Módulo 3: Endpoints y APIs

## Lección 1: ¿Qué es un endpoint?

Un **endpoint** es una **URL** a la que tu aplicación o el frontend hace una petición para obtener o enviar datos.

### Analogía
- **Endpoint** = Dirección de una tienda
- **Petición HTTP** = Entras y pides algo
- **Respuesta** = Te dan lo que pediste (o un error)

### Métodos HTTP comunes

| Método | Uso | Ejemplo |
|--------|-----|---------|
| **GET** | Obtener datos | Listar usuarios, ver producto |
| **POST** | Crear datos | Registrar usuario, crear pedido |
| **PUT/PATCH** | Actualizar datos | Modificar usuario |
| **DELETE** | Eliminar datos | Borrar registro |

---

## Ejemplo de endpoints

```
GET  /api/usuarios      → Devuelve lista de usuarios
GET  /api/usuarios/5    → Devuelve el usuario con id 5
POST /api/usuarios      → Crea un nuevo usuario
PUT  /api/usuarios/5    → Actualiza usuario 5
DELETE /api/usuarios/5  → Elimina usuario 5
```

---

## Lección 2: Respuestas en JSON

Las APIs normalmente devuelven **JSON** (formato de datos):

```json
{
    "id": 1,
    "nombre": "María",
    "email": "maria@email.com"
}
```

En PHP:
```php
$datos = ['nombre' => 'María', 'edad' => 25];
echo json_encode($datos);  // {"nombre":"María","edad":25}
```

Para recibir JSON en una petición POST:
```php
$input = json_decode(file_get_contents('php://input'), true);
```

---

## Lección 3: Códigos de estado HTTP

| Código | Significado |
|--------|-------------|
| 200 | OK - Todo bien |
| 201 | Creado - Recurso creado |
| 400 | Bad Request - Datos incorrectos |
| 404 | Not Found - No encontrado |
| 500 | Server Error - Error del servidor |

En PHP:
```php
http_response_code(200);  // Establece el código
```
