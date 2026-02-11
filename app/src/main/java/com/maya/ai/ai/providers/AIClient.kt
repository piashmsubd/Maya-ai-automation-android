package com.maya.ai.ai.providers

import com.maya.ai.data.models.Message

interface AIClient {
    suspend fun sendMessage(
        message: String,
        conversationHistory: List<Message> = emptyList()
    ): Result<String>

    suspend fun streamMessage(
        message: String,
        conversationHistory: List<Message> = emptyList(),
        onChunk: (String) -> Unit
    ): Result<Unit>

    fun isConfigured(): Boolean
}

sealed class AIResult {
    data class Success(val response: String) : AIResult()
    data class Error(val message: String, val throwable: Throwable? = null) : AIResult()
    data class Streaming(val chunk: String) : AIResult()
}
