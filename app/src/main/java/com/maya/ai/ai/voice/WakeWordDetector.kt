package com.maya.ai.ai.voice

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Simple wake word detector
 * Listens for wake words like "Hey Maya" in the recognized speech
 */
class WakeWordDetector(
    private val voiceRecognizer: VoiceRecognizer,
    private val wakeWords: List<String> = listOf("hey maya", "maya", "ok maya")
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var detectionJob: Job? = null

    private val _wakeWordDetected = MutableStateFlow(false)
    val wakeWordDetected: StateFlow<Boolean> = _wakeWordDetected

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening

    fun startDetection() {
        if (_isListening.value) return

        _isListening.value = true
        detectionJob = scope.launch {
            voiceRecognizer.voiceResults.collect { result ->
                when (result) {
                    is VoiceResult.Success -> {
                        checkForWakeWord(result.text)
                        // Restart listening for continuous detection
                        voiceRecognizer.startListening()
                    }
                    is VoiceResult.Partial -> {
                        checkForWakeWord(result.text)
                    }
                    is VoiceResult.Error -> {
                        // Restart on error (unless it's a fatal error)
                        if (!result.message.contains("INSUFFICIENT_PERMISSIONS")) {
                            kotlinx.coroutines.delay(1000)
                            if (_isListening.value) {
                                voiceRecognizer.startListening()
                            }
                        }
                    }
                    else -> { /* Ignore other events */ }
                }
            }
        }

        voiceRecognizer.startListening()
    }

    fun stopDetection() {
        _isListening.value = false
        detectionJob?.cancel()
        voiceRecognizer.stopListening()
        _wakeWordDetected.value = false
    }

    private fun checkForWakeWord(text: String) {
        val normalizedText = text.lowercase().trim()
        val detected = wakeWords.any { wakeWord ->
            normalizedText.contains(wakeWord)
        }

        if (detected && !_wakeWordDetected.value) {
            _wakeWordDetected.value = true
            // Reset after a short delay to allow for re-detection
            scope.launch {
                kotlinx.coroutines.delay(2000)
                _wakeWordDetected.value = false
            }
        }
    }

    fun destroy() {
        stopDetection()
    }
}
