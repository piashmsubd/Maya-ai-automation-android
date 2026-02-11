package com.maya.ai.ai.providers

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

/**
 * Cartesia AI Voice Client for realistic voice synthesis
 * Documentation: https://cartesia.ai/docs
 */
class CartesiaVoiceClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.cartesia.ai/v1"
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    /**
     * Synthesize speech from text
     * @param text The text to convert to speech
     * @param voiceId The voice ID to use (default: female English voice)
     * @param language The language code (en, bn, etc.)
     * @return ByteArray containing the audio data (MP3 format)
     */
    suspend fun synthesizeSpeech(
        text: String,
        voiceId: String = "default",
        language: String = "en"
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val requestBody = CartesiaRequest(
                text = text,
                voiceId = voiceId,
                language = language,
                outputFormat = "mp3"
            )

            val jsonBody = gson.toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/tts/synthesize")
                .addHeader("X-API-Key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Cartesia API Error: ${response.code}")
                )
            }

            val audioData = response.body?.bytes()
                ?: return@withContext Result.failure(IOException("Empty response from Cartesia"))

            Result.success(audioData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get available voices from Cartesia
     */
    suspend fun getAvailableVoices(): Result<List<Voice>> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/voices")
                .addHeader("X-API-Key", apiKey)
                .get()
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Cartesia API Error: ${response.code}")
                )
            }

            val voicesResponse = gson.fromJson(responseBody, VoicesResponse::class.java)
            Result.success(voicesResponse.voices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isConfigured(): Boolean {
        return apiKey.isNotBlank()
    }

    // Data classes for Cartesia API
    data class CartesiaRequest(
        val text: String,
        val voiceId: String,
        val language: String,
        val outputFormat: String
    )

    data class VoicesResponse(
        val voices: List<Voice>
    )

    data class Voice(
        val id: String,
        val name: String,
        val language: String,
        val gender: String,
        val description: String?
    )
}
