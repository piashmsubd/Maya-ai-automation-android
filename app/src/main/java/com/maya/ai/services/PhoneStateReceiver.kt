package com.maya.ai.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.maya.ai.data.datastore.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PhoneStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    handleIncomingCall(context, incomingNumber ?: "Unknown")
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    // Call answered
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    // Call ended
                }
            }
        } else if (intent.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            val outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
            handleOutgoingCall(context, outgoingNumber ?: "Unknown")
        }
    }

    private fun handleIncomingCall(context: Context, number: String) {
        val scope = CoroutineScope(Dispatchers.Main)
        val preferencesManager = PreferencesManager(context)

        scope.launch {
            val mayaActive = preferencesManager.mayaActive.first()
            if (mayaActive) {
                // Announce incoming call
                val contactName = getContactName(context, number) ?: number
                val message = "Sir, ekta call asche $contactName theke"
                // TODO: Get VoiceManager instance and speak
                // voiceManager.speak(message)
            }
        }
    }

    private fun handleOutgoingCall(context: Context, number: String) {
        // Handle outgoing call if needed
    }

    private fun getContactName(context: Context, phoneNumber: String): String? {
        // TODO: Implement contact lookup
        return null
    }
}
