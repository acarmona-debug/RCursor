# OyeTask (Android) – gestión de actividades con base local y voz

App Android (Kotlin + Jetpack Compose) para **gestionar actividades/tareas** con **base local (Room)**.
Incluye un modo de **dictado** para crear tareas diciendo frases como:

- “Oye task, tengo que hacer el reporte para mañana”
- “Oye task, debo pagar renta para 05/03”
- “Oye task, necesito llamar a Juan para 5 de marzo de 2026”

## Funcionalidades

- **Base local**: Room (`oyetask.db`) con persistencia offline.
- **UI dinámica** (Compose): lista, filtros (pendientes/hechas/todas), marcar como hecha y eliminar.
- **Alta rápida**: campo de texto + selector de fecha.
- **Alta por voz**: botón de micrófono (arriba a la derecha).

## Cómo ejecutar

1. Abre el repo en **Android Studio**.
2. Asegúrate de tener configurado el **Android SDK** (Android Studio genera `local.properties`).
3. Sincroniza Gradle y ejecuta en un dispositivo/emulador (minSdk 24).

## Notas del dictado

- El dictado usa el **reconocimiento de voz del sistema** (puede requerir servicios de Google, según el dispositivo).
- La app interpreta fechas “hoy”, “mañana”, `dd/mm(/yyyy)` y “\(d\) de \(mes\) (de \(año\))”.  
  Si no entiende la fecha, deja el texto en el campo para que lo ajustes manualmente.