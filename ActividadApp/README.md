# Actividad - Gestión de Actividades con Voz

App Android para gestionar tus actividades usando comandos de voz. Di *"Oye task, tengo que hacer [actividad] para [fecha]"* y la app añadirá la tarea automáticamente.

## Características

- **Voz por defecto**: Toca el botón del micrófono y dicta tus tareas
- **Lenguaje natural**: La app entiende fechas en español:
  - "mañana", "pasado mañana", "hoy"
  - "el lunes", "el martes", "próximo viernes"
  - "15 de marzo", "la próxima semana"
- **Base de datos local**: Room - todo se guarda en tu teléfono, sin nube
- **UI dinámica**: Jetpack Compose con animaciones y Material Design 3

## Cómo usar

1. Toca el botón del micrófono (permiso de audio la primera vez)
2. Di por ejemplo:
   - *"Tengo que hacer llamar al médico para mañana"*
   - *"Oye task, tengo que hacer enviar el informe para el viernes"*
   - *"Tengo que hacer comprar regalo para el 15 de marzo"*

## Requisitos

- Android 8.0 (API 26) o superior
- Conexión a internet para el reconocimiento de voz (Google)

## Configuración para desarrollo

1. Clona el repositorio
2. Crea `local.properties` en la raíz del proyecto con:
   ```
   sdk.dir=/ruta/a/tu/Android/Sdk
   ```
   O define la variable de entorno `ANDROID_HOME`

3. Build:
   ```bash
   ./gradlew assembleDebug
   ```

4. Instalar en dispositivo/emulador:
   ```bash
   ./gradlew installDebug
   ```

## Tecnologías

- **Kotlin** + **Jetpack Compose**
- **Room** - base de datos local
- **SpeechRecognizer** - reconocimiento de voz en español
- **Material Design 3**
