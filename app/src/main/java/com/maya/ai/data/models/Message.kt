package com.maya.ai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val conversationId: String = "default",
    val aiProvider: String = "openai", // openai, llama, letta, opencode
    val metadata: String? = null // JSON string for additional data
)
