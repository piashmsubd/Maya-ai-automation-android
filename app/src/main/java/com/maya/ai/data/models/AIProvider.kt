package com.maya.ai.data.models

enum class AIProvider {
    OPENAI,
    LLAMA_LOCAL,
    LETTA,
    OPENCODE_ZEN,
    CARTESIA // For voice synthesis
}

data class AIConfig(
    val provider: AIProvider,
    val apiKey: String? = null,
    val baseUrl: String? = null,
    val model: String? = null,
    val agentId: String? = null, // For Letta
    val temperature: Float = 0.7f,
    val maxTokens: Int = 2000
)
