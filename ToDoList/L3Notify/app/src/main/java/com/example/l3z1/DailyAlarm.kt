package com.example.l3z1

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class DailyAlarm() : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, "notify")
        GlobalScope.launch {
            val start = LocalDateTime.now().toString().take(10) + " 00:00"
            val end = LocalDateTime.now().toString().take(10) + " 23.59"
            val tasksList = DatabaseConnector.getApproaching(start, end) as ArrayList<Task>
            for (i in tasksList.indices) {
                if (tasksList[i].icon != null) {
                    val resId: Int = context.resources.getIdentifier(tasksList[i].icon, "drawable", context.packageName)
                    builder.setSmallIcon(resId)
                } else {
                    val resId: Int = context.resources.getIdentifier("ic_launcher_background", "drawable", context.packageName)
                    builder.setSmallIcon(resId)
                }
                builder.setContentTitle(tasksList[i].name)
                builder.setContentText("Deadline for completing the task is approaching")
                val int = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(context, 0, int, 0)
                builder.setContentIntent(pendingIntent)
                builder.setAutoCancel(true);
                builder.priority = NotificationCompat.PRIORITY_DEFAULT
                val notificationManager: NotificationManagerCompat = NotificationManagerCompat.from(context)
                notificationManager.notify(i, builder.build())
            }
        }
    }
}

class Notify(context: Context) {
    init {
        val channel = NotificationChannel("notify", "name", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager: NotificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        val intent = Intent(context, DailyAlarm::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 100, pendingIntent)
    }

    companion object {
        var INSTANCE: Notify? = null

        fun createNotify(context: Context) {
            if (INSTANCE == null) {
                synchronized(Notify::class) {
                    INSTANCE = Notify(context)
                }
            }
        }
    }

}