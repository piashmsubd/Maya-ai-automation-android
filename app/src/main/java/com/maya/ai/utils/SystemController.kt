package com.maya.ai.utils

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.getSystemService

/**
 * System Controller for managing system settings and features
 */
class SystemController(private val context: Context) {

    private val audioManager = context.getSystemService<AudioManager>()
    private val wifiManager = context.applicationContext.getSystemService<WifiManager>()

    // WiFi Control
    fun enableWifi(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ requires user interaction
                val intent = Intent(Settings.Panel.ACTION_WIFI)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            } else {
                @Suppress("DEPRECATION")
                wifiManager?.isWifiEnabled = true
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun disableWifi(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val intent = Intent(Settings.Panel.ACTION_WIFI)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                true
            } else {
                @Suppress("DEPRECATION")
                wifiManager?.isWifiEnabled = false
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun isWifiEnabled(): Boolean {
        return wifiManager?.isWifiEnabled ?: false
    }

    // Bluetooth Control
    fun toggleBluetooth(): Boolean {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        } else {
            Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        return true
    }

    // Volume Control
    fun setVolume(volume: Int, streamType: Int = AudioManager.STREAM_MUSIC) {
        audioManager?.setStreamVolume(streamType, volume, AudioManager.FLAG_SHOW_UI)
    }

    fun getVolume(streamType: Int = AudioManager.STREAM_MUSIC): Int {
        return audioManager?.getStreamVolume(streamType) ?: 0
    }

    fun getMaxVolume(streamType: Int = AudioManager.STREAM_MUSIC): Int {
        return audioManager?.getStreamMaxVolume(streamType) ?: 0
    }

    fun increaseVolume(streamType: Int = AudioManager.STREAM_MUSIC) {
        audioManager?.adjustStreamVolume(
            streamType,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    fun decreaseVolume(streamType: Int = AudioManager.STREAM_MUSIC) {
        audioManager?.adjustStreamVolume(
            streamType,
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }

    fun muteVolume(streamType: Int = AudioManager.STREAM_MUSIC) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager?.adjustStreamVolume(
                streamType,
                AudioManager.ADJUST_MUTE,
                0
            )
        }
    }

    fun unmuteVolume(streamType: Int = AudioManager.STREAM_MUSIC) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            audioManager?.adjustStreamVolume(
                streamType,
                AudioManager.ADJUST_UNMUTE,
                0
            )
        }
    }

    // Brightness Control
    fun setBrightness(brightness: Int) {
        try {
            val brightnessValue = brightness.coerceIn(0, 255)
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                brightnessValue
            )
        } catch (e: Exception) {
            // Need WRITE_SETTINGS permission
        }
    }

    fun getBrightness(): Int {
        return try {
            Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
        } catch (e: Exception) {
            0
        }
    }

    fun setAutoBrightness(enabled: Boolean) {
        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                if (enabled) Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
                else Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
            )
        } catch (e: Exception) {
            // Need WRITE_SETTINGS permission
        }
    }

    // Screen Rotation
    fun setAutoRotate(enabled: Boolean) {
        try {
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                if (enabled) 1 else 0
            )
        } catch (e: Exception) {
            // Need WRITE_SETTINGS permission
        }
    }

    // Airplane Mode
    fun toggleAirplaneMode() {
        val intent = Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    // Mobile Data
    fun toggleMobileData() {
        val intent = Intent(Settings.ACTION_DATA_ROAMING_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    // Location
    fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    // Do Not Disturb
    fun openDndSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    // Flashlight (requires camera)
    // Note: Flashlight control would require CameraManager and is device-specific

    // Screenshot (requires accessibility service or root)
    suspend fun takeScreenshot(): Boolean {
        return if (RootShell.isRootAvailable()) {
            val result = RootShell.execute("screencap -p /sdcard/screenshot.png")
            result.isSuccess
        } else {
            false
        }
    }

    // Screen Record (requires MediaProjection or root)
    // Implementation would require MediaProjection API

    // Device Info
    fun getDeviceInfo(): Map<String, String> {
        return mapOf(
            "brand" to Build.BRAND,
            "model" to Build.MODEL,
            "manufacturer" to Build.MANUFACTURER,
            "android_version" to Build.VERSION.RELEASE,
            "sdk_version" to Build.VERSION.SDK_INT.toString(),
            "device" to Build.DEVICE,
            "product" to Build.PRODUCT
        )
    }
}
