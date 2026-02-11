package com.maya.ai.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import com.maya.ai.ai.voice.VoiceManager
import com.maya.ai.data.datastore.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            messages.forEach { smsMessage ->
                handleIncomingSms(context, smsMessage)
            }
        }
    }

    private fun handleIncomingSms(context: Context, smsMessage: SmsMessage) {
        val sender = smsMessage.originatingAddress ?: "Unknown"
        val content = smsMessage.messageBody ?: ""
        
        val scope = CoroutineScope(Dispatchers.Main)
        val preferencesManager = PreferencesManager(context)
        
        scope.launch {
            val autoReadEnabled = preferencesManager.autoReadSms.first()
            val mayaActive = preferencesManager.mayaActive.first()
            
            if (autoReadEnabled && mayaActive) {
                // Speak the SMS notification
                val message = "Sir, ekta SMS esheche $sender theke. Message: $content"
                // TODO: Get VoiceManager instance and speak
                // voiceManager.speak(message)
                
                // Optionally ask for reply
                val autoReply = preferencesManager.smsAutoReply.first()
                if (autoReply) {
                    // TODO: Implement auto-reply logic with AI
                }
            }
        }
    }
}
