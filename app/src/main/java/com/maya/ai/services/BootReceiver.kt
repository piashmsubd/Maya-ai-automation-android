package com.maya.ai.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maya.ai.data.datastore.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            val scope = CoroutineScope(Dispatchers.Main)
            val preferencesManager = PreferencesManager(context)

            scope.launch {
                val autoStartEnabled = preferencesManager.autoStartOnBoot.first()
                if (autoStartEnabled) {
                    // Start VoiceAssistantService
                    val serviceIntent = Intent(context, VoiceAssistantService::class.java)
                    context.startForegroundService(serviceIntent)

                    // Start FloatingBubbleService if enabled
                    val floatingBubbleEnabled = preferencesManager.floatingBubbleEnabled.first()
                    if (floatingBubbleEnabled) {
                        val bubbleIntent = Intent(context, FloatingBubbleService::class.java)
                        context.startForegroundService(bubbleIntent)
                    }
                }
            }
        }
    }
}
