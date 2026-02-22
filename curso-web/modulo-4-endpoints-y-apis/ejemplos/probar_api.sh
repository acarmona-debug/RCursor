#!/bin/bash
# ============================================================
# Script para probar la API de productos
# ============================================================
# Primero inicia el servidor PHP en otra terminal:
#   cd modulo-4-endpoints-y-apis/ejemplos
#   php -S localhost:8000
#
# Luego ejecuta este script:
#   bash probar_api.sh
# ============================================================

BASE="http://localhost:8000/api_productos.php"

echo "=== Probando API de Productos ==="
echo ""

# 1. GET: Listar todos los productos
echo "--- 1. GET: Todos los productos ---"
curl -s "$BASE" | python3 -m json.tool 2>/dev/null || curl -s "$BASE"
echo ""

# 2. POST: Crear un producto nuevo
echo "--- 2. POST: Crear producto ---"
curl -s -X POST "$BASE" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Audífonos Bluetooth","precio":890,"cantidad_en_stock":15,"categoria":"Electrónica"}' \
  | python3 -m json.tool 2>/dev/null || echo "Ver respuesta arriba"
echo ""

# 3. GET: Ver producto específico (id=1)
echo "--- 3. GET: Producto id=1 ---"
curl -s "$BASE?id=1" | python3 -m json.tool 2>/dev/null || curl -s "$BASE?id=1"
echo ""

# 4. PUT: Actualizar producto
echo "--- 4. PUT: Actualizar producto id=1 ---"
curl -s -X PUT "$BASE?id=1" \
  -H "Content-Type: application/json" \
  -d '{"precio":13000,"cantidad_en_stock":8}' \
  | python3 -m json.tool 2>/dev/null || echo "Ver respuesta arriba"
echo ""

# 5. GET: Filtrar por categoría
echo "--- 5. GET: Filtrar por categoría ---"
curl -s "$BASE?categoria=Electrónica" | python3 -m json.tool 2>/dev/null || curl -s "$BASE?categoria=Electrónica"
echo ""

# 6. DELETE: Eliminar producto
echo "--- 6. DELETE: Eliminar producto id=5 ---"
curl -s -X DELETE "$BASE?id=5" | python3 -m json.tool 2>/dev/null || curl -s -X DELETE "$BASE?id=5"
echo ""

echo "=== Pruebas completadas ==="
