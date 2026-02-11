package com.maya.ai.ai.voice

import android.content.Context
import com.maya.ai.ai.AIManager
import com.maya.ai.data.datastore.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Central manager for all voice-related functionality
 * Coordinates speech recognition, synthesis, wake word detection, and AI responses
 */
class VoiceManager(
    private val context: Context,
    private val aiManager: AIManager,
    private val preferencesManager: PreferencesManager
) {
    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val voiceRecognizer = VoiceRecognizer(context)
    private val voiceSynthesizer = VoiceSynthesizer(context)
    private val wakeWordDetector = WakeWordDetector(voiceRecognizer)
    private val audioPlayer = AudioPlayer()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking

    private val _currentTranscript = MutableStateFlow("")
    val currentTranscript: StateFlow<String> = _currentTranscript

    init {
        // Initialize TTS
        voiceSynthesizer.initialize { success ->
            if (!success) {
                // Handle TTS initialization failure
            }
        }

        // Listen to wake word detection
        scope.launch {
            wakeWordDetector.wakeWordDetected.collect { detected ->
                if (detected) {
                    onWakeWordDetected()
                }
            }
        }

        // Listen to voice recognition results
        scope.launch {
            voiceRecognizer.voiceResults.collect { result ->
                handleVoiceResult(result)
            }
        }

        // Listen to TTS events
        scope.launch {
            voiceSynthesizer.events.collect { event ->
                when (event) {
                    is TTSEvent.Started -> _isSpeaking.value = true
                    is TTSEvent.Completed -> _isSpeaking.value = false
                    is TTSEvent.Error -> _isSpeaking.value = false
                }
            }
        }
    }

    suspend fun startWakeWordDetection() {
        val wakeWordEnabled = preferencesManager.wakeWordEnabled.first()
        if (wakeWordEnabled) {
            wakeWordDetector.startDetection()
        }
    }

    fun stopWakeWordDetection() {
        wakeWordDetector.stopDetection()
    }

    fun startListening() {
        _isListening.value = true
        scope.launch {
            val language = preferencesManager.ttsLanguage.first()
            voiceRecognizer.startListening(language)
        }
    }

    fun stopListening() {
        _isListening.value = false
        voiceRecognizer.stopListening()
        _currentTranscript.value = ""
    }

    fun speak(text: String) {
        scope.launch {
            val language = preferencesManager.ttsLanguage.first()
            val speed = preferencesManager.ttsSpeed.first()
            voiceSynthesizer.speak(text, language, speed)
        }
    }

    fun speakWithCartesia(text: String) {
        scope.launch {
            val language = preferencesManager.ttsLanguage.first()
            val result = aiManager.synthesizeSpeech(text, language)
            
            result.fold(
                onSuccess = { audioData ->
                    audioPlayer.playAudio(audioData)
                    _isSpeaking.value = true
                },
                onFailure = {
                    // Fallback to Android TTS
                    speak(text)
                }
            )
        }
    }

    fun stopSpeaking() {
        voiceSynthesizer.stop()
        audioPlayer.stop()
        _isSpeaking.value = false
    }

    private fun onWakeWordDetected() {
        // Play acknowledgment sound or speak
        scope.launch {
            speak("Yes?")
            kotlinx.coroutines.delay(1000)
            startListening()
        }
    }

    private fun handleVoiceResult(result: VoiceResult) {
        when (result) {
            is VoiceResult.Success -> {
                _currentTranscript.value = result.text
                processVoiceCommand(result.text)
            }
            is VoiceResult.Partial -> {
                _currentTranscript.value = result.text
            }
            is VoiceResult.Error -> {
                _currentTranscript.value = ""
                _isListening.value = false
            }
            is VoiceResult.SpeechEnded -> {
                _isListening.value = false
            }
            else -> { /* Ignore other events */ }
        }
    }

    private fun processVoiceCommand(command: String) {
        scope.launch {
            // Send to AI for processing
            val result = aiManager.sendMessage(command)
            
            result.fold(
                onSuccess = { response ->
                    // Speak the AI response
                    val ttsEnabled = preferencesManager.ttsEnabled.first()
                    if (ttsEnabled) {
                        speak(response)
                    }
                    
                    // Check if continuous listening is enabled
                    val continuousListening = preferencesManager.continuousListening.first()
                    if (continuousListening) {
                        kotlinx.coroutines.delay(2000)
                        startListening()
                    }
                },
                onFailure = { error ->
                    speak("Sorry, I encountered an error: ${error.message}")
                }
            )
        }
    }

    fun destroy() {
        stopListening()
        stopWakeWordDetection()
        voiceRecognizer.destroy()
        voiceSynthesizer.shutdown()
        audioPlayer.stop()
    }
}
