package com.prometheus.seniorcare.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.prometheus.seniorcare.MainActivity
import com.prometheus.seniorcare.R
import kotlinx.coroutines.*

class DailyCheckService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    private val channelId = "daily_check_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(1, createNotification())

        // Schedule daily check at 10:00 AM
        scheduleDailyCheck()

        return START_STICKY
    }

    private fun scheduleDailyCheck() {
        serviceScope.launch {
            while (isActive) {
                delay(24 * 60 * 60 * 1000) // 24 hours

                // Send notification for daily check
                sendDailyCheckNotification()
            }
        }
    }

    private fun sendDailyCheckNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Prometheus - Dienos patikrinimas")
            .setContentText("Paspauskite, kad patvirtintumėte, kad esate gerai")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(100, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Check Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for daily safety check"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Prometheus SeniorCare")
            .setContentText("Saugumo tarnyba veikia")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
