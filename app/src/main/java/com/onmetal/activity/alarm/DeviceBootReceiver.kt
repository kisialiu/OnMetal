package com.onmetal.activity.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.onmetal.constant.serviceTime

class DeviceBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action.equals("android.intent.action.BOOT_COMPLETED")) {
            val alarmIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)

            val manager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), serviceTime, pendingIntent)
        }
    }
}