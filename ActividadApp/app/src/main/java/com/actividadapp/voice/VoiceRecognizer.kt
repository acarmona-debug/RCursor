package com.actividadapp.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.*

class VoiceRecognizer(private val context: Context) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening = false

    var onResult: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onListeningChanged: ((Boolean) -> Unit)? = null

    fun iniciarEscucha() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onError?.invoke("El reconocimiento de voz no está disponible")
            return
        }

        speechRecognizer?.destroy()
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    isListening = true
                    onListeningChanged?.invoke(true)
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListening = false
                    onListeningChanged?.invoke(false)
                }
                override fun onError(error: Int) {
                    isListening = false
                    onListeningChanged?.invoke(false)
                    val mensaje = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                        SpeechRecognizer.ERROR_CLIENT -> "Error del cliente"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permisos insuficientes"
                        SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No se reconoció nada. Intenta de nuevo."
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Reconocedor ocupado"
                        SpeechRecognizer.ERROR_SERVER -> "Error del servidor"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo agotado. Habla de nuevo."
                        else -> "Error desconocido"
                    }
                    onError?.invoke(mensaje)
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val mejor = matches?.firstOrNull()
                    if (!mejor.isNullOrBlank()) {
                        onResult?.invoke(mejor)
                    } else {
                        onError?.invoke("No se entendió. Di: \"Tengo que hacer [actividad] para [fecha]\"")
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "ES"))
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Di tu tarea. Ejemplo: Tengo que hacer llamar al médico para mañana")
        }

        speechRecognizer?.startListening(intent)
    }

    fun detener() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isListening = false
        onListeningChanged?.invoke(false)
    }

    fun estaEscuchando() = isListening
}
