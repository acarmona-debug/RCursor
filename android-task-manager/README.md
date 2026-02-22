# Task Voice Manager (Android)

Aplicacion Android (Kotlin + Jetpack Compose) para gestionar actividades con:

- Creacion manual de actividades
- Captura por voz estilo "Oye task..."
- Parseo basico de fecha (hoy, manana, pasado manana o dd/MM/yyyy)
- Base de datos local con Room
- Lista dinamica para completar y eliminar

## Estructura

- `app/src/main/java/com/example/taskvoicemanager/data`: Room (entidad, DAO, DB, repositorio)
- `app/src/main/java/com/example/taskvoicemanager/voice`: parser de comandos por voz
- `app/src/main/java/com/example/taskvoicemanager/ui`: ViewModel + Compose UI

## Ejemplos de voz

- "Oye task, tengo que entregar reporte para 28/02/2026"
- "Oye task necesito llamar al cliente manana"
- "Debo enviar correo hoy"

## Abrir en Android Studio

1. Abre la carpeta `android-task-manager`.
2. Deja que Gradle sincronice.
3. Ejecuta en emulador o dispositivo Android.

> Nota: El reconocimiento de voz usa `RecognizerIntent`, por lo que depende de que el dispositivo tenga servicio de dictado instalado.
