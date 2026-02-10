package com.prometheus.seniorcare.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.prometheus.seniorcare.services.DailyCheckService

/**
 * Receiver that starts the DailyCheckService after device boot.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val serviceIntent = Intent(context, DailyCheckService::class.java)
            context.startService(serviceIntent)
        }
    }
}
