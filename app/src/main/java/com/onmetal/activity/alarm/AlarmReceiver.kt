package com.onmetal.activity.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.onmetal.R
import com.onmetal.activity.main.MainActivity
import com.onmetal.task.GetTodayReleasesTask
import com.onmetal.util.getLikedBands
import io.reactivex.Single

class AlarmReceiver : BroadcastReceiver() {

    private val channelId = "com.onmental.channel"
    private val channelName = "Latest Releases channel"

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            getTodayReleases(context)
        }
    }

    private fun getTodayReleases(context: Context) {
        GetTodayReleasesTask(this, context).execute()
    }

    fun sendNotification(context: Context, result: List<String>) {
        Single.fromCallable({
            var contains = false
            val liked = getLikedBands(context)
            for (id in result) {
                if (liked.contains(id)) {
                    contains = true
                }
            }

            if (contains) {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                    notificationManager.createNotificationChannel(channel)
                }

                val builder = NotificationCompat.Builder(context, channelId)
                        .setContentTitle("Your latest releases")
                        .setContentText("One of your liked bands has new release! Go and check in OnMetal!")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    builder.setSmallIcon(R.drawable.ic_small_icon)
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher_round)
                }

                notificationManager.notify(1, builder.build())
            }
        })

    }
}