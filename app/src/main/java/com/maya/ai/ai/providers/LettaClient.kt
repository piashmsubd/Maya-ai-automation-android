package com.maya.ai.ai.providers

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.maya.ai.data.models.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class LettaClient(
    private val apiKey: String,
    private val agentId: String,
    private val baseUrl: String = "https://api.letta.com"
) : AIClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    override suspend fun sendMessage(
        message: String,
        conversationHistory: List<Message>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val requestBody = LettaRequest(
                message = message,
                agentId = agentId
            )

            val jsonBody = gson.toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/v1/agents/$agentId/messages")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("Letta API Error: ${response.code} - $responseBody")
                )
            }

            val lettaResponse = gson.fromJson(responseBody, LettaResponse::class.java)
            val content = lettaResponse.messages.firstOrNull()?.text
                ?: return@withContext Result.failure(IOException("Empty response from Letta"))

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun streamMessage(
        message: String,
        conversationHistory: List<Message>,
        onChunk: (String) -> Unit
    ): Result<Unit> {
        // Letta doesn't support streaming in the same way, so we'll just send and return the full message
        return try {
            val result = sendMessage(message, conversationHistory)
            result.fold(
                onSuccess = { 
                    onChunk(it)
                    Result.success(Unit)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isConfigured(): Boolean {
        return apiKey.isNotBlank() && agentId.isNotBlank()
    }

    // Data classes for Letta API
    data class LettaRequest(
        val message: String,
        @SerializedName("agent_id")
        val agentId: String
    )

    data class LettaResponse(
        val messages: List<LettaMessage>
    )

    data class LettaMessage(
        val text: String,
        val role: String
    )
}
