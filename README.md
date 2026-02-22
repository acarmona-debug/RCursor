# OyTask - Gestión de Actividades para Android

Una app Android moderna e interactiva para gestionar tus actividades y tareas. Con entrada por voz, puedes decir _"Oye Task, tengo que entregar el reporte para el viernes"_ y la app creará la tarea automáticamente.

## Características

- **Entrada por voz** - Dicta tus tareas en español y la app las interpreta automáticamente (título, fecha límite, prioridad)
- **Base de datos local** - Todas tus tareas se almacenan localmente con Room/SQLite, sin necesidad de internet
- **Interfaz moderna** - Material Design 3, tema oscuro, animaciones fluidas, chips de filtrado
- **Gestión completa** - Crear, editar, completar, eliminar tareas con deslizamiento (swipe)
- **Filtros inteligentes** - Todas, Pendientes, Hoy, Vencidas, Esta Semana, Completadas
- **Búsqueda rápida** - Busca por título o descripción
- **Prioridades** - Baja, Media, Alta, Urgente con indicador visual de color
- **Fechas relativas** - Muestra "Hoy", "Mañana", "Viernes" en lugar de fechas absolutas
- **Deshacer eliminación** - Snackbar con opción de deshacer al eliminar una tarea

## Arquitectura

- **Kotlin** - Lenguaje principal
- **MVVM** - Model-View-ViewModel con LiveData
- **Room** - Base de datos local SQLite
- **Coroutines** - Operaciones asíncronas
- **Material Components** - UI moderna y consistente
- **ViewBinding** - Acceso seguro a vistas
- **SpeechRecognizer** - Reconocimiento de voz nativo de Android

## Cómo funciona la voz

La app interpreta frases en español y extrae:
- **Título**: Lo que necesitas hacer
- **Fecha**: "para mañana", "para el viernes", "en 3 días", "15 de marzo"
- **Prioridad**: "urgente", "importante", "baja prioridad"

### Ejemplos de comandos de voz:
- _"Tengo que entregar el proyecto para el viernes"_
- _"Comprar leche para mañana"_
- _"Hacer la presentación urgente para el lunes"_
- _"Llamar al doctor en 3 días"_

## Requisitos

- Android 8.0 (API 26) o superior
- Permiso de micrófono para entrada por voz

## Compilar

```bash
./gradlew assembleDebug
```

## Estructura del proyecto

```
app/src/main/java/com/oytask/app/
├── OyTaskApp.kt                    # Application class
├── data/
│   ├── db/
│   │   ├── Converters.kt           # Room type converters
│   │   ├── TaskDao.kt              # Data Access Object
│   │   └── TaskDatabase.kt         # Room database
│   ├── model/
│   │   ├── Task.kt                 # Task entity
│   │   └── TaskFilter.kt           # Filter & sort enums
│   └── repository/
│       └── TaskRepository.kt       # Data repository
├── ui/
│   ├── adapter/
│   │   ├── SwipeToDeleteCallback.kt # Swipe gesture handler
│   │   └── TaskAdapter.kt          # RecyclerView adapter
│   ├── dialog/
│   │   └── TaskDialogFragment.kt   # Add/edit bottom sheet
│   ├── main/
│   │   └── MainActivity.kt         # Main screen
│   └── viewmodel/
│       └── TaskViewModel.kt        # ViewModel
└── util/
    ├── DateUtils.kt                # Date formatting utilities
    └── VoiceParser.kt              # Voice input parser
```
