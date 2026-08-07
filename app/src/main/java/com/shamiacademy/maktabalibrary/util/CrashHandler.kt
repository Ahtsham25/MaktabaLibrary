package com.shamiacademy.maktabalibrary.util

import android.content.Context
import android.os.Build
import android.os.Environment
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Catches any uncaught exception app-wide and writes the full stack trace,
 * device info, and timestamp into a plain-text file under:
 *   Android/data/<package>/files/Download/MaktabaLibrary/CrashLogs/
 * so it survives the crash and can be opened, copied, or shared afterwards
 * (e.g. sent back for debugging) without needing logcat/a computer.
 *
 * Install once, as early as possible, from Application.onCreate().
 */
class CrashHandler(private val context: Context) : Thread.UncaughtExceptionHandler {

    private val defaultHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    companion object {
        private const val FOLDER = "MaktabaLibrary/CrashLogs"

        fun install(context: Context) {
            Thread.setDefaultUncaughtExceptionHandler(CrashHandler(context.applicationContext))
        }

        fun logsDir(context: Context): File {
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), FOLDER)
            if (!dir.exists()) dir.mkdirs()
            return dir
        }

        fun listLogs(context: Context): List<File> =
            logsDir(context).listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            writeCrashFile(thread, throwable)
        } catch (e: Exception) {
            // If even the crash logger fails, fall through silently —
            // we must not throw again inside an exception handler.
        } finally {
            // Hand off to the system's default handler so the app still
            // shows the normal "has stopped" dialog / closes as expected.
            defaultHandler?.uncaughtException(thread, throwable) ?: android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    private fun writeCrashFile(thread: Thread, throwable: Throwable) {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))

        val timeFmt = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US)
        val readableTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val fileName = "crash_${timeFmt.format(Date())}.txt"
        val file = File(logsDir(context), fileName)

        val content = buildString {
            appendLine("مکتبۃ العلماء — Crash Report")
            appendLine("Time: $readableTime")
            appendLine("Thread: ${thread.name}")
            appendLine("App version: ${getVersionInfo()}")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL} (Android ${Build.VERSION.RELEASE}, SDK ${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine("---- Exception ----")
            appendLine(sw.toString())
        }

        file.writeText(content)
    }

    private fun getVersionInfo(): String {
        return try {
            val pm = context.packageManager
            val pkgInfo = pm.getPackageInfo(context.packageName, 0)
            val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkgInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pkgInfo.versionCode.toLong()
            }
            "${pkgInfo.versionName} ($code)"
        } catch (e: Exception) {
            "unknown"
        }
    }
}
