package com.maya.ai.ai.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Locale

class VoiceRecognizer(private val context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val resultChannel = Channel<VoiceResult>(Channel.BUFFERED)
    
    val voiceResults: Flow<VoiceResult> = resultChannel.receiveAsFlow()

    fun startListening(language: String = "en-US") {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            resultChannel.trySend(VoiceResult.Error("Speech recognition not available"))
            return
        }

        stopListening()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    resultChannel.trySend(VoiceResult.Ready)
                }

                override fun onBeginningOfSpeech() {
                    resultChannel.trySend(VoiceResult.SpeechStarted)
                }

                override fun onRmsChanged(rmsdB: Float) {
                    resultChannel.trySend(VoiceResult.RmsChanged(rmsdB))
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Not used
                }

                override fun onEndOfSpeech() {
                    resultChannel.trySend(VoiceResult.SpeechEnded)
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
                        SpeechRecognizer.ERROR_NETWORK -> "Network error"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
                        SpeechRecognizer.ERROR_SERVER -> "Server error"
                        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
                        else -> "Unknown error: $error"
                    }
                    resultChannel.trySend(VoiceResult.Error(errorMessage))
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val confidence = results?.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES)
                    
                    if (matches != null && matches.isNotEmpty()) {
                        resultChannel.trySend(
                            VoiceResult.Success(
                                text = matches[0],
                                confidence = confidence?.get(0) ?: 0f,
                                alternatives = matches
                            )
                        )
                    } else {
                        resultChannel.trySend(VoiceResult.Error("No results"))
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (matches != null && matches.isNotEmpty()) {
                        resultChannel.trySend(VoiceResult.Partial(matches[0]))
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    // Not used
                }
            })

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }

            startListening(intent)
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    fun destroy() {
        stopListening()
        resultChannel.close()
    }
}

sealed class VoiceResult {
    object Ready : VoiceResult()
    object SpeechStarted : VoiceResult()
    object SpeechEnded : VoiceResult()
    data class RmsChanged(val rmsdB: Float) : VoiceResult()
    data class Success(
        val text: String,
        val confidence: Float,
        val alternatives: List<String>
    ) : VoiceResult()
    data class Partial(val text: String) : VoiceResult()
    data class Error(val message: String) : VoiceResult()
}
