package com.maya.ai.ai.voice

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.Locale

class VoiceSynthesizer(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val eventChannel = Channel<TTSEvent>(Channel.BUFFERED)
    
    val events: Flow<TTSEvent> = eventChannel.receiveAsFlow()

    fun initialize(onInitialized: (Boolean) -> Unit) {
        tts = TextToSpeech(context) { status ->
            isInitialized = status == TextToSpeech.SUCCESS
            if (isInitialized) {
                tts?.language = Locale.US
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        eventChannel.trySend(TTSEvent.Started(utteranceId ?: ""))
                    }

                    override fun onDone(utteranceId: String?) {
                        eventChannel.trySend(TTSEvent.Completed(utteranceId ?: ""))
                    }

                    override fun onError(utteranceId: String?) {
                        eventChannel.trySend(TTSEvent.Error(utteranceId ?: "", "TTS Error"))
                    }
                })
            }
            onInitialized(isInitialized)
        }
    }

    fun speak(
        text: String,
        language: String = "en-US",
        speed: Float = 1.0f,
        utteranceId: String = "maya_${System.currentTimeMillis()}"
    ) {
        if (!isInitialized) {
            eventChannel.trySend(TTSEvent.Error(utteranceId, "TTS not initialized"))
            return
        }

        tts?.apply {
            // Set language
            val locale = when (language) {
                "bn-BD", "bn" -> Locale("bn", "BD")
                "en-US", "en" -> Locale.US
                "en-GB" -> Locale.UK
                else -> Locale.US
            }
            
            val result = setLanguage(locale)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                eventChannel.trySend(TTSEvent.Error(utteranceId, "Language not supported: $language"))
                return
            }

            // Set speech rate
            setSpeechRate(speed)

            // Speak
            speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
        eventChannel.close()
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking ?: false
    }

    fun getAvailableLanguages(): Set<Locale>? {
        return tts?.availableLanguages
    }
}

sealed class TTSEvent {
    data class Started(val utteranceId: String) : TTSEvent()
    data class Completed(val utteranceId: String) : TTSEvent()
    data class Error(val utteranceId: String, val message: String) : TTSEvent()
}

/**
 * Audio player for playing synthesized audio from Cartesia or other sources
 */
class AudioPlayer {
    private var audioTrack: AudioTrack? = null

    fun playAudio(audioData: ByteArray, sampleRate: Int = 44100) {
        stop()

        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        audioTrack?.play()
        audioTrack?.write(audioData, 0, audioData.size)
    }

    fun stop() {
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
    }

    fun isPlaying(): Boolean {
        return audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING
    }
}
