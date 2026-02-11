package com.maya.ai.utils

import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Root Shell Manager using LibSU
 * Provides root access functionality for system-level operations
 */
object RootShell {

    private var isRootAvailable: Boolean? = null

    init {
        // Configure shell
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setTimeout(10)
        )
    }

    /**
     * Check if root access is available
     */
    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        if (isRootAvailable == null) {
            isRootAvailable = Shell.isAppGrantedRoot() ?: false
        }
        isRootAvailable ?: false
    }

    /**
     * Execute a root command
     */
    suspend fun execute(command: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!isRootAvailable()) {
                return@withContext Result.failure(
                    SecurityException("Root access not available")
                )
            }

            val result = Shell.cmd(command).exec()
            
            if (result.isSuccess) {
                val output = result.out.joinToString("\n")
                Result.success(output)
            } else {
                val error = result.err.joinToString("\n")
                Result.failure(RuntimeException("Command failed: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Execute multiple root commands
     */
    suspend fun execute(commands: List<String>): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            if (!isRootAvailable()) {
                return@withContext Result.failure(
                    SecurityException("Root access not available")
                )
            }

            val result = Shell.cmd(*commands.toTypedArray()).exec()
            
            if (result.isSuccess) {
                Result.success(result.out)
            } else {
                Result.failure(RuntimeException("Commands failed: ${result.err.joinToString("\n")}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Execute command and return exit code
     */
    suspend fun executeWithExitCode(command: String): Pair<Int, String> = withContext(Dispatchers.IO) {
        if (!isRootAvailable()) {
            return@withContext Pair(-1, "Root access not available")
        }

        val result = Shell.cmd(command).exec()
        Pair(result.code, result.out.joinToString("\n"))
    }

    /**
     * Common root operations
     */
    object Operations {
        suspend fun rebootDevice() = execute("reboot")
        
        suspend fun rebootRecovery() = execute("reboot recovery")
        
        suspend fun rebootBootloader() = execute("reboot bootloader")
        
        suspend fun shutdownDevice() = execute("reboot -p")
        
        suspend fun killApp(packageName: String) = execute("am force-stop $packageName")
        
        suspend fun clearAppData(packageName: String) = execute("pm clear $packageName")
        
        suspend fun uninstallApp(packageName: String) = execute("pm uninstall $packageName")
        
        suspend fun installApk(apkPath: String) = execute("pm install -r $apkPath")
        
        suspend fun grantPermission(packageName: String, permission: String) = 
            execute("pm grant $packageName $permission")
        
        suspend fun revokePermission(packageName: String, permission: String) = 
            execute("pm revoke $packageName $permission")
        
        suspend fun setSystemProperty(key: String, value: String) = 
            execute("setprop $key $value")
        
        suspend fun getSystemProperty(key: String) = 
            execute("getprop $key")
        
        suspend fun mountSystemRW() = execute("mount -o rw,remount /system")
        
        suspend fun mountSystemRO() = execute("mount -o ro,remount /system")
        
        suspend fun deleteFile(path: String) = execute("rm -f $path")
        
        suspend fun deleteDirectory(path: String) = execute("rm -rf $path")
        
        suspend fun copyFile(source: String, destination: String) = 
            execute("cp $source $destination")
        
        suspend fun moveFile(source: String, destination: String) = 
            execute("mv $source $destination")
        
        suspend fun changePermissions(path: String, permissions: String) = 
            execute("chmod $permissions $path")
        
        suspend fun changeOwner(path: String, owner: String) = 
            execute("chown $owner $path")
        
        suspend fun createDirectory(path: String) = 
            execute("mkdir -p $path")
        
        suspend fun listDirectory(path: String) = 
            execute("ls -la $path")
        
        suspend fun readFile(path: String) = 
            execute("cat $path")
        
        suspend fun writeFile(path: String, content: String) = 
            execute("echo '$content' > $path")
        
        suspend fun appendToFile(path: String, content: String) = 
            execute("echo '$content' >> $path")
    }
}
