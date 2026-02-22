# M0 - Fundamentos web y HTTP

## Mini teoria (ultra corta)

- **Cliente**: navegador, app movil o script que pide informacion.
- **Servidor**: programa que recibe la peticion y responde.
- **Request**: lo que envia el cliente (metodo, ruta, headers, body).
- **Response**: lo que devuelve el servidor (status, headers, body).

Ejemplo mental:
1. Tu navegador pide `GET /productos`.
2. El servidor busca datos.
3. Responde `200 OK` con JSON o HTML.

## Metodos HTTP esenciales

- `GET`: leer datos
- `POST`: crear datos
- `PUT` o `PATCH`: actualizar datos
- `DELETE`: borrar datos

## Codigos de estado comunes

- `200` OK (peticion exitosa)
- `201` Created (recurso creado)
- `400` Bad Request (datos invalidos)
- `404` Not Found (ruta o recurso no existe)
- `500` Internal Server Error (error del servidor)

## Ejercicio clave 1 (conceptual)

Responde en texto:

1. Si quiero listar usuarios, que metodo usaria y por que?
2. Si quiero crear un usuario, que metodo usaria y por que?
3. Que diferencia hay entre `request body` y `query params`?
4. Que codigo de estado devolverias si falta el campo `email` en un POST?
5. Que codigo de estado devolverias si no existe el usuario ID 99?

## Ejercicio clave 2 (matching rapido)

Relaciona:

- A) GET
- B) POST
- C) PUT
- D) DELETE

Con:

1. Borrar un producto
2. Crear una orden
3. Obtener lista de ordenes
4. Actualizar perfil de usuario

## Ejercicio clave 3 (request/response)

Imagina endpoint: `POST /login`

Entrada JSON:

```json
{
  "email": "user@example.com",
  "password": "123456"
}
```

Si el login es correcto, escribe una respuesta JSON posible y status code.
Si falla, escribe otra respuesta JSON y status code.

## Para enviarme por chat

Mandame:
- Respuestas del ejercicio 1
- Matching del ejercicio 2
- Respuesta correcta/error del ejercicio 3

Cuando lo hagas, escribe: **Hecho M0**
