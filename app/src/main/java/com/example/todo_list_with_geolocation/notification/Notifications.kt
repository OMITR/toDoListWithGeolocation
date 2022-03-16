package com.example.todo_list_with_geolocation.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todo_list_with_geolocation.R
import com.example.todo_list_with_geolocation.ui.MainActivity

const val CHANNEL_ID = "channel1"
const val NOTIFICATION_ID = 1
const val TITLE_EXTRA = "Reminder"
const val TASK_EXTRA = "Task"

class Notifications : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(TITLE_EXTRA))
            .setContentText(intent.getStringExtra(TASK_EXTRA))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}