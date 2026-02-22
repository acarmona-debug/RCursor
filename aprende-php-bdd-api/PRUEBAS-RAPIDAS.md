# 🧪 Pruebas Rápidas - Cómo ejecutar todo

## 1. Verificar PHP
```bash
php -v
```
Debe mostrar PHP 7.4 o superior.

---

## 2. Probar PHP básico
```bash
cd modulo-2-php
php ejemplo-basico.php
php ejercicio-2-1-solucion.php
```

---

## 3. Crear la base de datos de usuarios
```bash
cd modulo-2-php
php conectar-y-listar-solucion.php
```
Esto crea `datos.db` con la tabla usuarios.

---

## 4. Iniciar servidor para probar la API
```bash
cd aprende-php-bdd-api/modulo-3-endpoints
php -S localhost:8000
```
Deja esta terminal abierta. En **otra terminal** o en el navegador:

### Probar API simple
```bash
curl http://localhost:8000/api-simple.php
```

### Probar API con base de datos
```bash
# Listar usuarios
curl http://localhost:8000/api-con-bdd.php?action=listar

# Ver usuario id 1
curl http://localhost:8000/api-con-bdd.php?action=ver&id=1

# Crear usuario
curl -X POST http://localhost:8000/api-con-bdd.php \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Nuevo","email":"nuevo@test.com"}'
```

### Probar API productos
```bash
curl "http://localhost:8000/api-productos-solucion.php?precio_minimo=50"
```

---

## 5. En el navegador

Abre: `http://localhost:8000/api-simple.php`

Verás el JSON formateado (si tienes extensión JSON) o el texto plano.
