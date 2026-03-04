package com.english.accelerator.ai.downloader

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.english.accelerator.MainActivity
import com.english.accelerator.R
import kotlinx.coroutines.*

/**
 * 模型下载前台服务
 *
 * 支持后台下载，即使应用切换到后台或被杀死也能继续下载
 */
class DownloadService : Service() {

    private val binder = DownloadBinder()
    private var dManager: DManager? = null
    private var downloadJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var currentProgress: Float = 0f
    private var currentSpeed: Long = 0L
    private var isDownloading = false

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "model_download_channel"
        private const val CHANNEL_NAME = "模型下载"

        const val ACTION_START_DOWNLOAD = "com.english.accelerator.START_DOWNLOAD"
        const val ACTION_PAUSE_DOWNLOAD = "com.english.accelerator.PAUSE_DOWNLOAD"
        const val ACTION_RESUME_DOWNLOAD = "com.english.accelerator.RESUME_DOWNLOAD"
        const val ACTION_CANCEL_DOWNLOAD = "com.english.accelerator.CANCEL_DOWNLOAD"

        fun startDownload(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun pauseDownload(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_PAUSE_DOWNLOAD
            }
            context.startService(intent)
        }

        fun resumeDownload(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_RESUME_DOWNLOAD
            }
            context.startService(intent)
        }

        fun cancelDownload(context: Context) {
            val intent = Intent(context, DownloadService::class.java).apply {
                action = ACTION_CANCEL_DOWNLOAD
            }
            context.startService(intent)
        }
    }

    inner class DownloadBinder : Binder() {
        fun getService(): DownloadService = this@DownloadService
    }

    override fun onCreate() {
        super.onCreate()
        dManager = DManager(this)
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> startDownload()
            ACTION_PAUSE_DOWNLOAD -> pauseDownload()
            ACTION_RESUME_DOWNLOAD -> resumeDownload()
            ACTION_CANCEL_DOWNLOAD -> cancelDownload()
        }
        return START_STICKY
    }

    private fun startDownload() {
        if (isDownloading) return

        isDownloading = true
        startForeground(NOTIFICATION_ID, createNotification("准备下载...", 0f))

        downloadJob = serviceScope.launch {
            dManager?.downloadModel { downloaded, total, speed ->
                currentSpeed = speed
                currentProgress = if (total > 0) downloaded.toFloat() / total.toFloat() else 0f
                updateNotification("下载中...", currentProgress)

                // 更新配置文件中的速度信息
                dManager?.updateDownloadSpeed(speed)
            }?.onSuccess {
                isDownloading = false
                updateNotification("下载完成", 1f)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }?.onFailure { error ->
                isDownloading = false
                updateNotification("下载失败: ${error.message}", currentProgress)
                delay(3000)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun pauseDownload() {
        dManager?.pauseDownload()
        isDownloading = false
        updateNotification("已暂停", currentProgress)
    }

    private fun resumeDownload() {
        if (isDownloading) return

        isDownloading = true
        dManager?.resumeDownload()

        downloadJob = serviceScope.launch {
            dManager?.downloadModel { downloaded, total, speed ->
                currentSpeed = speed
                currentProgress = if (total > 0) downloaded.toFloat() / total.toFloat() else 0f
                updateNotification("下载中...", currentProgress)

                // 更新配置文件中的速度信息
                dManager?.updateDownloadSpeed(speed)
            }?.onSuccess {
                isDownloading = false
                updateNotification("下载完成", 1f)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }?.onFailure { error ->
                isDownloading = false
                updateNotification("下载失败: ${error.message}", currentProgress)
                delay(3000)
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun cancelDownload() {
        dManager?.cancelDownload()
        downloadJob?.cancel()
        isDownloading = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示模型下载进度"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(text: String, progress: Float): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("模型下载")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)

        if (progress > 0f) {
            builder.setProgress(100, (progress * 100).toInt(), false)
            if (currentSpeed > 0) {
                builder.setSubText(formatSpeed(currentSpeed))
            }
        } else {
            builder.setProgress(100, 0, true)
        }

        return builder.build()
    }

    private fun updateNotification(text: String, progress: Float) {
        val notification = createNotification(text, progress)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun formatSpeed(bytesPerSecond: Long): String {
        return when {
            bytesPerSecond < 1024 -> "${bytesPerSecond} B/s"
            bytesPerSecond < 1024 * 1024 -> String.format("%.1f KB/s", bytesPerSecond / 1024.0)
            else -> String.format("%.1f MB/s", bytesPerSecond / (1024.0 * 1024.0))
        }
    }

    fun getCurrentProgress(): Float = currentProgress
    fun getCurrentSpeed(): Long = currentSpeed
    fun isDownloading(): Boolean = isDownloading

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
        serviceScope.cancel()
    }
}
