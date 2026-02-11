package com.maya.ai.data.models

data class SmsMessage(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val conversationId: String? = null
)

data class MessengerMessage(
    val id: String,
    val sender: String,
    val content: String,
    val timestamp: Long,
    val platform: MessengerPlatform,
    val isRead: Boolean = false
)

enum class MessengerPlatform {
    WHATSAPP,
    FACEBOOK_MESSENGER,
    TELEGRAM,
    SMS
}
