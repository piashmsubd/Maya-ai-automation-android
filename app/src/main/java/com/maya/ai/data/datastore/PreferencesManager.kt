package com.maya.ai.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.maya.ai.data.models.AIProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maya_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        // AI Settings
        private val CURRENT_AI_PROVIDER = stringPreferencesKey("current_ai_provider")
        private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        private val OPENAI_BASE_URL = stringPreferencesKey("openai_base_url")
        private val OPENAI_MODEL = stringPreferencesKey("openai_model")
        private val LETTA_API_KEY = stringPreferencesKey("letta_api_key")
        private val LETTA_AGENT_ID = stringPreferencesKey("letta_agent_id")
        private val LETTA_BASE_URL = stringPreferencesKey("letta_base_url")
        private val CARTESIA_API_KEY = stringPreferencesKey("cartesia_api_key")
        
        // Voice Settings
        private val WAKE_WORD_ENABLED = booleanPreferencesKey("wake_word_enabled")
        private val WAKE_WORD = stringPreferencesKey("wake_word")
        private val TTS_ENABLED = booleanPreferencesKey("tts_enabled")
        private val TTS_LANGUAGE = stringPreferencesKey("tts_language")
        private val TTS_SPEED = floatPreferencesKey("tts_speed")
        private val CONTINUOUS_LISTENING = booleanPreferencesKey("continuous_listening")
        
        // App Settings
        private val FLOATING_BUBBLE_ENABLED = booleanPreferencesKey("floating_bubble_enabled")
        private val AUTO_START_ON_BOOT = booleanPreferencesKey("auto_start_on_boot")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val MAYA_ACTIVE = booleanPreferencesKey("maya_active")
        
        // SMS/Notification Settings
        private val AUTO_READ_SMS = booleanPreferencesKey("auto_read_sms")
        private val SMS_AUTO_REPLY = booleanPreferencesKey("sms_auto_reply")
        private val MESSENGER_NOTIFICATIONS = booleanPreferencesKey("messenger_notifications")
        
        // Permissions
        private val ROOT_ACCESS_ENABLED = booleanPreferencesKey("root_access_enabled")
    }

    // AI Provider
    val currentAIProvider: Flow<AIProvider> = context.dataStore.data.map { prefs ->
        AIProvider.valueOf(prefs[CURRENT_AI_PROVIDER] ?: AIProvider.OPENAI.name)
    }

    suspend fun setCurrentAIProvider(provider: AIProvider) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_AI_PROVIDER] = provider.name
        }
    }

    // OpenAI Settings
    val openAIApiKey: Flow<String?> = context.dataStore.data.map { it[OPENAI_API_KEY] }
    suspend fun setOpenAIApiKey(key: String) {
        context.dataStore.edit { it[OPENAI_API_KEY] = key }
    }

    val openAIBaseUrl: Flow<String> = context.dataStore.data.map { 
        it[OPENAI_BASE_URL] ?: "https://api.openai.com/v1" 
    }
    suspend fun setOpenAIBaseUrl(url: String) {
        context.dataStore.edit { it[OPENAI_BASE_URL] = url }
    }

    val openAIModel: Flow<String> = context.dataStore.data.map { 
        it[OPENAI_MODEL] ?: "gpt-4" 
    }
    suspend fun setOpenAIModel(model: String) {
        context.dataStore.edit { it[OPENAI_MODEL] = model }
    }

    // Letta Settings
    val lettaApiKey: Flow<String?> = context.dataStore.data.map { it[LETTA_API_KEY] }
    suspend fun setLettaApiKey(key: String) {
        context.dataStore.edit { it[LETTA_API_KEY] = key }
    }

    val lettaAgentId: Flow<String?> = context.dataStore.data.map { it[LETTA_AGENT_ID] }
    suspend fun setLettaAgentId(id: String) {
        context.dataStore.edit { it[LETTA_AGENT_ID] = id }
    }

    val lettaBaseUrl: Flow<String> = context.dataStore.data.map { 
        it[LETTA_BASE_URL] ?: "https://api.letta.com" 
    }
    suspend fun setLettaBaseUrl(url: String) {
        context.dataStore.edit { it[LETTA_BASE_URL] = url }
    }

    // Cartesia Settings
    val cartesiaApiKey: Flow<String?> = context.dataStore.data.map { it[CARTESIA_API_KEY] }
    suspend fun setCartesiaApiKey(key: String) {
        context.dataStore.edit { it[CARTESIA_API_KEY] = key }
    }

    // Voice Settings
    val wakeWordEnabled: Flow<Boolean> = context.dataStore.data.map { 
        it[WAKE_WORD_ENABLED] ?: true 
    }
    suspend fun setWakeWordEnabled(enabled: Boolean) {
        context.dataStore.edit { it[WAKE_WORD_ENABLED] = enabled }
    }

    val wakeWord: Flow<String> = context.dataStore.data.map { 
        it[WAKE_WORD] ?: "Hey Maya" 
    }
    suspend fun setWakeWord(word: String) {
        context.dataStore.edit { it[WAKE_WORD] = word }
    }

    val ttsEnabled: Flow<Boolean> = context.dataStore.data.map { 
        it[TTS_ENABLED] ?: true 
    }
    suspend fun setTtsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[TTS_ENABLED] = enabled }
    }

    val ttsLanguage: Flow<String> = context.dataStore.data.map { 
        it[TTS_LANGUAGE] ?: "en-US" 
    }
    suspend fun setTtsLanguage(language: String) {
        context.dataStore.edit { it[TTS_LANGUAGE] = language }
    }

    val ttsSpeed: Flow<Float> = context.dataStore.data.map { 
        it[TTS_SPEED] ?: 1.0f 
    }
    suspend fun setTtsSpeed(speed: Float) {
        context.dataStore.edit { it[TTS_SPEED] = speed }
    }

    val continuousListening: Flow<Boolean> = context.dataStore.data.map { 
        it[CONTINUOUS_LISTENING] ?: false 
    }
    suspend fun setContinuousListening(enabled: Boolean) {
        context.dataStore.edit { it[CONTINUOUS_LISTENING] = enabled }
    }

    // App Settings
    val floatingBubbleEnabled: Flow<Boolean> = context.dataStore.data.map { 
        it[FLOATING_BUBBLE_ENABLED] ?: false 
    }
    suspend fun setFloatingBubbleEnabled(enabled: Boolean) {
        context.dataStore.edit { it[FLOATING_BUBBLE_ENABLED] = enabled }
    }

    val autoStartOnBoot: Flow<Boolean> = context.dataStore.data.map { 
        it[AUTO_START_ON_BOOT] ?: false 
    }
    suspend fun setAutoStartOnBoot(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_START_ON_BOOT] = enabled }
    }

    val darkMode: Flow<Boolean> = context.dataStore.data.map { 
        it[DARK_MODE] ?: true 
    }
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    val mayaActive: Flow<Boolean> = context.dataStore.data.map { 
        it[MAYA_ACTIVE] ?: false 
    }
    suspend fun setMayaActive(active: Boolean) {
        context.dataStore.edit { it[MAYA_ACTIVE] = active }
    }

    // SMS/Notification Settings
    val autoReadSms: Flow<Boolean> = context.dataStore.data.map { 
        it[AUTO_READ_SMS] ?: false 
    }
    suspend fun setAutoReadSms(enabled: Boolean) {
        context.dataStore.edit { it[AUTO_READ_SMS] = enabled }
    }

    val smsAutoReply: Flow<Boolean> = context.dataStore.data.map { 
        it[SMS_AUTO_REPLY] ?: false 
    }
    suspend fun setSmsAutoReply(enabled: Boolean) {
        context.dataStore.edit { it[SMS_AUTO_REPLY] = enabled }
    }

    val messengerNotifications: Flow<Boolean> = context.dataStore.data.map { 
        it[MESSENGER_NOTIFICATIONS] ?: true 
    }
    suspend fun setMessengerNotifications(enabled: Boolean) {
        context.dataStore.edit { it[MESSENGER_NOTIFICATIONS] = enabled }
    }

    // Root Access
    val rootAccessEnabled: Flow<Boolean> = context.dataStore.data.map { 
        it[ROOT_ACCESS_ENABLED] ?: false 
    }
    suspend fun setRootAccessEnabled(enabled: Boolean) {
        context.dataStore.edit { it[ROOT_ACCESS_ENABLED] = enabled }
    }

    // Clear all data
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}
