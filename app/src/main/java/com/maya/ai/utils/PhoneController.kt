package com.maya.ai.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Telephony
import android.telecom.TelecomManager
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService

/**
 * Phone Controller for making calls, sending SMS, and managing contacts
 */
class PhoneController(private val context: Context) {

    private val contentResolver: ContentResolver = context.contentResolver
    private val telecomManager = context.getSystemService<TelecomManager>()

    /**
     * Make a phone call
     */
    fun makeCall(phoneNumber: String): Boolean {
        return try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) 
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }

            val intent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Make call by contact name
     */
    fun makeCallByName(name: String): Boolean {
        val contact = findContactByName(name)
        return contact?.phoneNumber?.let { makeCall(it) } ?: false
    }

    /**
     * End current call (requires Android 9+ or root)
     */
    fun endCall(): Boolean {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS)
                    == PackageManager.PERMISSION_GRANTED) {
                    telecomManager?.endCall()
                    true
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Send SMS
     */
    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                return false
            }

            val smsManager = android.telephony.SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Send SMS by contact name
     */
    fun sendSmsByName(name: String, message: String): Boolean {
        val contact = findContactByName(name)
        return contact?.phoneNumber?.let { sendSms(it, message) } ?: false
    }

    /**
     * Get recent SMS messages
     */
    fun getRecentSms(limit: Int = 10): List<SmsInfo> {
        val messages = mutableListOf<SmsInfo>()
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            return messages
        }

        val cursor: Cursor? = contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            arrayOf(
                Telephony.Sms._ID,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE
            ),
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < limit) {
                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                messages.add(
                    SmsInfo(
                        address = address,
                        body = body,
                        timestamp = date,
                        isIncoming = type == Telephony.Sms.MESSAGE_TYPE_INBOX
                    )
                )
                count++
            }
        }

        return messages
    }

    /**
     * Find contact by name
     */
    fun findContactByName(name: String): ContactInfo? {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            return null
        }

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?",
            arrayOf("%$name%"),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val contactName = it.getString(0)
                val phoneNumber = it.getString(1)
                return ContactInfo(contactName, phoneNumber)
            }
        }

        return null
    }

    /**
     * Get all contacts
     */
    fun getAllContacts(): List<ContactInfo> {
        val contacts = mutableListOf<ContactInfo>()
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            return contacts
        }

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(0)
                val number = it.getString(1)
                contacts.add(ContactInfo(name, number))
            }
        }

        return contacts
    }

    /**
     * Get call history
     */
    fun getCallHistory(limit: Int = 10): List<CallInfo> {
        val calls = mutableListOf<CallInfo>()
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED) {
            return calls
        }

        val cursor: Cursor? = contentResolver.query(
            android.provider.CallLog.Calls.CONTENT_URI,
            arrayOf(
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.TYPE,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.DURATION
            ),
            null,
            null,
            "${android.provider.CallLog.Calls.DATE} DESC"
        )

        cursor?.use {
            var count = 0
            while (it.moveToNext() && count < limit) {
                val number = it.getString(0)
                val type = it.getInt(1)
                val date = it.getLong(2)
                val duration = it.getInt(3)

                val callType = when (type) {
                    android.provider.CallLog.Calls.INCOMING_TYPE -> "Incoming"
                    android.provider.CallLog.Calls.OUTGOING_TYPE -> "Outgoing"
                    android.provider.CallLog.Calls.MISSED_TYPE -> "Missed"
                    else -> "Unknown"
                }

                calls.add(CallInfo(number, callType, date, duration))
                count++
            }
        }

        return calls
    }

    data class SmsInfo(
        val address: String,
        val body: String,
        val timestamp: Long,
        val isIncoming: Boolean
    )

    data class ContactInfo(
        val name: String,
        val phoneNumber: String
    )

    data class CallInfo(
        val number: String,
        val type: String,
        val timestamp: Long,
        val duration: Int
    )
}
