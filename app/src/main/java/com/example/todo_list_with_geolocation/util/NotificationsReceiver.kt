package com.example.todo_list_with_geolocation.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.todo_list_with_geolocation.R
import com.example.todo_list_with_geolocation.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.*

const val CHANNEL_ID = "channel1"
const val TITLE_EXTRA = "Reminder"
const val TASK_EXTRA = "Task"
var NOTIFICATION_ID = 0
const val GROUP_KEY_NOTIFICATION = "com.android.example.todo_list_with_geolocation"

class NotificationsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NOTIFICATION_ID = createID()
        val resultIntent = Intent(context, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(resultIntent)
            getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_MUTABLE)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(intent.getStringExtra(TITLE_EXTRA))
            .setContentText(intent.getStringExtra(TASK_EXTRA))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(resultPendingIntent)
            .setAutoCancel(true)
            .setGroup(GROUP_KEY_NOTIFICATION)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

}