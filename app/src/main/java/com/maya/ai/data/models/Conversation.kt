package com.maya.ai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class Conversation(
    @PrimaryKey
    val id: String,
    val title: String,
    val lastMessage: String,
    val lastMessageTime: Long = System.currentTimeMillis(),
    val messageCount: Int = 0,
    val aiProvider: String = "openai"
)
