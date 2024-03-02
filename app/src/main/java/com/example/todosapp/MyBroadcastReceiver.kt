package com.example.todosapp

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.Calendar

class MyBroadcastReceiver : BroadcastReceiver() {

    private val CHANNEL_ID = "Task Reminder"

    override fun onReceive(context: Context?, intent: Intent?) {
        val taskName = intent?.getStringExtra("taskName")
        val id = intent?.getIntExtra("id", -10)

        if (id!! >= 0){
            val mainActivityIntent = Intent(context, MainActivity::class.java)
            mainActivityIntent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val penIntent = PendingIntent.getActivity(
                context,
                0,
                mainActivityIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context!!, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(taskName)
                .setContentText("Its time for: $taskName")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(penIntent)

            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        context,
                        "Notification Permission Not Provided!",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                this.notify(id, builder.build())
            }
        }
        else if (id == -1) {
            val db: LocalDB = LocalDB.getDB(context)
            val taskList = db.taskDao().allTask.toMutableList()
            for (i in taskList)
            {
                i.freq++
                if (i.rep == 1 || (i.rep == 2 && i.freq % 7 == 0) || (i.rep == 3 && i.freq % 30 == 0))
                {
                    i.status = 0
                    if (i.calTIM.toString() != "-1")
                    {
                        val calendar = Calendar.getInstance()
                        calendar.apply {
                            timeInMillis = i.calTIM
                            set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                        }
                        RecyclerTaskAdapter.setAlarm(i.id, context!!)
                    }
                }
                db.taskDao().editTask(i)
            }
        }
    }
}
