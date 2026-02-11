package com.maya.ai.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.getSystemService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * App Controller for managing installed applications
 */
class AppController(private val context: Context) {

    private val packageManager = context.packageManager
    private val activityManager = context.getSystemService<ActivityManager>()

    /**
     * Get list of all installed apps
     */
    fun getInstalledApps(): List<AppInfo> {
        val apps = mutableListOf<AppInfo>()
        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        packages.forEach { appInfo ->
            apps.add(
                AppInfo(
                    packageName = appInfo.packageName,
                    appName = packageManager.getApplicationLabel(appInfo).toString(),
                    isSystemApp = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                )
            )
        }
        
        return apps.sortedBy { it.appName }
    }

    /**
     * Find app by name (fuzzy search)
     */
    fun findAppByName(name: String): AppInfo? {
        val apps = getInstalledApps()
        
        // Exact match
        apps.find { it.appName.equals(name, ignoreCase = true) }?.let { return it }
        
        // Contains match
        apps.find { it.appName.contains(name, ignoreCase = true) }?.let { return it }
        
        return null
    }

    /**
     * Launch app by package name
     */
    fun launchApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Launch app by name
     */
    fun launchAppByName(appName: String): Boolean {
        val app = findAppByName(appName)
        return app?.let { launchApp(it.packageName) } ?: false
    }

    /**
     * Force stop app
     */
    suspend fun forceStopApp(packageName: String): Boolean {
        return if (RootShell.isRootAvailable()) {
            val result = RootShell.Operations.killApp(packageName)
            result.isSuccess
        } else {
            // Without root, we can only open app settings
            openAppSettings(packageName)
            false
        }
    }

    /**
     * Clear app data
     */
    suspend fun clearAppData(packageName: String): Boolean {
        return if (RootShell.isRootAvailable()) {
            val result = RootShell.Operations.clearAppData(packageName)
            result.isSuccess
        } else {
            openAppSettings(packageName)
            false
        }
    }

    /**
     * Uninstall app
     */
    fun uninstallApp(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Open app settings
     */
    fun openAppSettings(packageName: String): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Get running apps
     */
    fun getRunningApps(): List<String> {
        val runningApps = mutableListOf<String>()
        try {
            activityManager?.getRunningServices(Integer.MAX_VALUE)?.forEach { service ->
                runningApps.add(service.service.packageName)
            }
        } catch (e: Exception) {
            // May require special permissions
        }
        return runningApps.distinct()
    }

    /**
     * Check if app is installed
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Open app in Play Store
     */
    fun openInPlayStore(packageName: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to browser
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    data class AppInfo(
        val packageName: String,
        val appName: String,
        val isSystemApp: Boolean
    )
}
