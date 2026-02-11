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

class OpenAIClient(
    private val apiKey: String,
    private val baseUrl: String = "https://api.openai.com/v1",
    private val model: String = "gpt-4"
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
            val messages = buildMessageList(message, conversationHistory)
            val requestBody = OpenAIRequest(
                model = model,
                messages = messages,
                temperature = 0.7,
                maxTokens = 2000
            )

            val jsonBody = gson.toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("OpenAI API Error: ${response.code} - $responseBody")
                )
            }

            val openAIResponse = gson.fromJson(responseBody, OpenAIResponse::class.java)
            val content = openAIResponse.choices.firstOrNull()?.message?.content
                ?: return@withContext Result.failure(IOException("Empty response from OpenAI"))

            Result.success(content)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun streamMessage(
        message: String,
        conversationHistory: List<Message>,
        onChunk: (String) -> Unit
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val messages = buildMessageList(message, conversationHistory)
            val requestBody = OpenAIRequest(
                model = model,
                messages = messages,
                temperature = 0.7,
                maxTokens = 2000,
                stream = true
            )

            val jsonBody = gson.toJson(requestBody)
            val request = Request.Builder()
                .url("$baseUrl/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(jsonBody.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    IOException("OpenAI API Error: ${response.code}")
                )
            }

            response.body?.source()?.let { source ->
                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: continue
                    if (line.startsWith("data: ") && !line.contains("[DONE]")) {
                        val json = line.removePrefix("data: ")
                        try {
                            val chunk = gson.fromJson(json, OpenAIStreamResponse::class.java)
                            val content = chunk.choices.firstOrNull()?.delta?.content
                            content?.let { onChunk(it) }
                        } catch (e: Exception) {
                            // Skip malformed chunks
                        }
                    }
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isConfigured(): Boolean {
        return apiKey.isNotBlank()
    }

    private fun buildMessageList(
        currentMessage: String,
        history: List<Message>
    ): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()
        
        // System message
        messages.add(
            ChatMessage(
                role = "system",
                content = "You are Maya, a helpful AI assistant. You can control the phone, read messages, make calls, and help with various tasks. Be concise and friendly. Support both English and Bengali."
            )
        )

        // Add conversation history
        history.takeLast(10).forEach { msg ->
            messages.add(
                ChatMessage(
                    role = if (msg.isFromUser) "user" else "assistant",
                    content = msg.content
                )
            )
        }

        // Add current message
        messages.add(ChatMessage(role = "user", content = currentMessage))

        return messages
    }

    // Data classes for OpenAI API
    data class OpenAIRequest(
        val model: String,
        val messages: List<ChatMessage>,
        val temperature: Double = 0.7,
        @SerializedName("max_tokens")
        val maxTokens: Int = 2000,
        val stream: Boolean = false
    )

    data class ChatMessage(
        val role: String,
        val content: String
    )

    data class OpenAIResponse(
        val choices: List<Choice>
    )

    data class Choice(
        val message: ChatMessage
    )

    data class OpenAIStreamResponse(
        val choices: List<StreamChoice>
    )

    data class StreamChoice(
        val delta: Delta
    )

    data class Delta(
        val content: String?
    )
}
