package com.maya.ai.ai

import android.content.Context
import com.maya.ai.ai.providers.*
import com.maya.ai.data.datastore.PreferencesManager
import com.maya.ai.data.models.AIProvider
import com.maya.ai.data.models.Message
import kotlinx.coroutines.flow.first

class AIManager(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    private var currentClient: AIClient? = null
    private var cartesiaClient: CartesiaVoiceClient? = null

    suspend fun initializeClient() {
        val provider = preferencesManager.currentAIProvider.first()
        
        currentClient = when (provider) {
            AIProvider.OPENAI -> {
                val apiKey = preferencesManager.openAIApiKey.first() ?: ""
                val baseUrl = preferencesManager.openAIBaseUrl.first()
                val model = preferencesManager.openAIModel.first()
                OpenAIClient(apiKey, baseUrl, model)
            }
            AIProvider.LLAMA_LOCAL -> {
                LlamaLocalClient(context)
            }
            AIProvider.LETTA -> {
                val apiKey = preferencesManager.lettaApiKey.first() ?: ""
                val agentId = preferencesManager.lettaAgentId.first() ?: ""
                val baseUrl = preferencesManager.lettaBaseUrl.first()
                LettaClient(apiKey, agentId, baseUrl)
            }
            AIProvider.OPENCODE_ZEN -> {
                // OpenCode Zen would be similar to OpenAI client with different base URL
                val apiKey = preferencesManager.openAIApiKey.first() ?: ""
                OpenAIClient(apiKey, "https://api.opencode.dev/v1", "opencode-zen")
            }
            AIProvider.CARTESIA -> {
                // Cartesia is primarily for voice, not chat
                null
            }
        }

        // Initialize Cartesia for voice synthesis
        val cartesiaKey = preferencesManager.cartesiaApiKey.first()
        if (!cartesiaKey.isNullOrBlank()) {
            cartesiaClient = CartesiaVoiceClient(cartesiaKey)
        }
    }

    suspend fun sendMessage(
        message: String,
        conversationHistory: List<Message> = emptyList()
    ): Result<String> {
        if (currentClient == null) {
            initializeClient()
        }

        return currentClient?.sendMessage(message, conversationHistory)
            ?: Result.failure(IllegalStateException("AI client not initialized"))
    }

    suspend fun streamMessage(
        message: String,
        conversationHistory: List<Message> = emptyList(),
        onChunk: (String) -> Unit
    ): Result<Unit> {
        if (currentClient == null) {
            initializeClient()
        }

        return currentClient?.streamMessage(message, conversationHistory, onChunk)
            ?: Result.failure(IllegalStateException("AI client not initialized"))
    }

    suspend fun synthesizeSpeech(text: String, language: String = "en"): Result<ByteArray> {
        if (cartesiaClient == null) {
            val cartesiaKey = preferencesManager.cartesiaApiKey.first()
            if (!cartesiaKey.isNullOrBlank()) {
                cartesiaClient = CartesiaVoiceClient(cartesiaKey)
            } else {
                return Result.failure(IllegalStateException("Cartesia client not configured"))
            }
        }

        return cartesiaClient?.synthesizeSpeech(text, language = language)
            ?: Result.failure(IllegalStateException("Cartesia client not available"))
    }

    fun isConfigured(): Boolean {
        return currentClient?.isConfigured() ?: false
    }

    suspend fun switchProvider(provider: AIProvider) {
        preferencesManager.setCurrentAIProvider(provider)
        initializeClient()
    }
}
