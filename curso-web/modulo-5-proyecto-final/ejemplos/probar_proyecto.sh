#!/bin/bash
# ============================================================
# Script para probar el proyecto final completo
# ============================================================
# Primero inicia el servidor:
#   cd modulo-5-proyecto-final/ejemplos
#   php -S localhost:8000
#
# Luego ejecuta: bash probar_proyecto.sh
# ============================================================

BASE="http://localhost:8000/api"
echo "=========================================="
echo "  Probando API del Proyecto Final"
echo "=========================================="

# 1. Listar usuarios
echo ""
echo "--- 1. GET /api/usuarios ---"
curl -s "$BASE/usuarios.php" | python3 -m json.tool 2>/dev/null

# 2. Crear un usuario
echo ""
echo "--- 2. POST /api/usuarios ---"
curl -s -X POST "$BASE/usuarios.php" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Pedro Ramírez","email":"pedro@email.com"}' \
  | python3 -m json.tool 2>/dev/null

# 3. Listar todas las tareas
echo ""
echo "--- 3. GET /api/tareas ---"
curl -s "$BASE/tareas.php" | python3 -m json.tool 2>/dev/null

# 4. Crear una tarea
echo ""
echo "--- 4. POST /api/tareas ---"
curl -s -X POST "$BASE/tareas.php" \
  -H "Content-Type: application/json" \
  -d '{"usuario_id":1,"titulo":"Terminar proyecto final","descripcion":"Completar todos los endpoints","prioridad":"alta","fecha_limite":"2026-03-01"}' \
  | python3 -m json.tool 2>/dev/null

# 5. Filtrar tareas por estado
echo ""
echo "--- 5. GET /api/tareas?estado=pendiente ---"
curl -s "$BASE/tareas.php?estado=pendiente" | python3 -m json.tool 2>/dev/null

# 6. Filtrar tareas por usuario
echo ""
echo "--- 6. GET /api/tareas?usuario_id=1 ---"
curl -s "$BASE/tareas.php?usuario_id=1" | python3 -m json.tool 2>/dev/null

# 7. Actualizar estado de una tarea
echo ""
echo "--- 7. PUT /api/tareas?id=3 (marcar como completada) ---"
curl -s -X PUT "$BASE/tareas.php?id=3" \
  -H "Content-Type: application/json" \
  -d '{"estado":"completada"}' \
  | python3 -m json.tool 2>/dev/null

# 8. Ver estadísticas
echo ""
echo "--- 8. GET /api/estadisticas ---"
curl -s "$BASE/estadisticas.php" | python3 -m json.tool 2>/dev/null

# 9. Eliminar una tarea
echo ""
echo "--- 9. DELETE /api/tareas?id=7 ---"
curl -s -X DELETE "$BASE/tareas.php?id=7" | python3 -m json.tool 2>/dev/null

echo ""
echo "=========================================="
echo "  Pruebas completadas"
echo "=========================================="
