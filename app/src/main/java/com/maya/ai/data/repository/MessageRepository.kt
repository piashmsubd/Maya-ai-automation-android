package com.maya.ai.data.repository

import com.maya.ai.data.database.MessageDao
import com.maya.ai.data.database.ConversationDao
import com.maya.ai.data.models.Message
import com.maya.ai.data.models.Conversation
import kotlinx.coroutines.flow.Flow

class MessageRepository(
    private val messageDao: MessageDao,
    private val conversationDao: ConversationDao
) {
    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId)
    }

    fun getRecentMessages(limit: Int = 100): Flow<List<Message>> {
        return messageDao.getRecentMessages(limit)
    }

    suspend fun saveMessage(message: Message) {
        messageDao.insertMessage(message)
        updateConversation(message)
    }

    suspend fun deleteMessage(message: Message) {
        messageDao.deleteMessage(message)
    }

    suspend fun deleteAllMessages() {
        messageDao.deleteAllMessages()
    }

    private suspend fun updateConversation(message: Message) {
        val conversation = conversationDao.getConversationById(message.conversationId)
        if (conversation != null) {
            val updatedConversation = conversation.copy(
                lastMessage = message.content,
                lastMessageTime = message.timestamp,
                messageCount = conversation.messageCount + 1
            )
            conversationDao.updateConversation(updatedConversation)
        } else {
            val newConversation = Conversation(
                id = message.conversationId,
                title = "Conversation",
                lastMessage = message.content,
                lastMessageTime = message.timestamp,
                messageCount = 1,
                aiProvider = message.aiProvider
            )
            conversationDao.insertConversation(newConversation)
        }
    }

    // Conversation methods
    fun getAllConversations(): Flow<List<Conversation>> {
        return conversationDao.getAllConversations()
    }

    suspend fun getConversationById(id: String): Conversation? {
        return conversationDao.getConversationById(id)
    }

    suspend fun deleteConversation(conversation: Conversation) {
        messageDao.deleteMessagesForConversation(conversation.id)
        conversationDao.deleteConversation(conversation)
    }

    suspend fun deleteAllConversations() {
        messageDao.deleteAllMessages()
        conversationDao.deleteAllConversations()
    }
}
