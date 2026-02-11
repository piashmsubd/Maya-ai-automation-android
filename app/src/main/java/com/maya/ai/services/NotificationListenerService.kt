package com.maya.ai.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.maya.ai.data.datastore.PreferencesManager
import com.maya.ai.data.models.MessengerPlatform
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var preferencesManager: PreferencesManager

    companion object {
        private const val WHATSAPP_PACKAGE = "com.whatsapp"
        private const val FACEBOOK_MESSENGER_PACKAGE = "com.facebook.orca"
        private const val TELEGRAM_PACKAGE = "org.telegram.messenger"
    }

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferencesManager(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return

        scope.launch {
            val mayaActive = preferencesManager.mayaActive.first()
            val messengerNotificationsEnabled = preferencesManager.messengerNotifications.first()

            if (!mayaActive || !messengerNotificationsEnabled) return@launch

            when (sbn.packageName) {
                WHATSAPP_PACKAGE -> handleMessengerNotification(sbn, MessengerPlatform.WHATSAPP)
                FACEBOOK_MESSENGER_PACKAGE -> handleMessengerNotification(sbn, MessengerPlatform.FACEBOOK_MESSENGER)
                TELEGRAM_PACKAGE -> handleMessengerNotification(sbn, MessengerPlatform.TELEGRAM)
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Handle notification removal if needed
    }

    private fun handleMessengerNotification(sbn: StatusBarNotification, platform: MessengerPlatform) {
        val notification = sbn.notification
        val extras = notification.extras

        val title = extras.getCharSequence("android.title")?.toString() ?: ""
        val text = extras.getCharSequence("android.text")?.toString() ?: ""

        if (title.isNotBlank() && text.isNotBlank()) {
            val platformName = when (platform) {
                MessengerPlatform.WHATSAPP -> "WhatsApp"
                MessengerPlatform.FACEBOOK_MESSENGER -> "Facebook Messenger"
                MessengerPlatform.TELEGRAM -> "Telegram"
                MessengerPlatform.SMS -> "SMS"
            }

            val message = "Sir, $platformName e ekta message esheche $title theke. Message: $text"
            // TODO: Get VoiceManager instance and speak
            // voiceManager.speak(message)
        }
    }

    /**
     * Reply to a messenger notification
     * This would require accessibility service to automate the reply
     */
    fun replyToNotification(platform: MessengerPlatform, replyText: String) {
        // TODO: Use accessibility service to:
        // 1. Open the messenger app
        // 2. Find the conversation
        // 3. Type the reply
        // 4. Send the message
    }
}
