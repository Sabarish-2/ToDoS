package com.example.todosapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.todosapp.RecyclerTaskAdapter.Companion.setAlarm
import java.util.Calendar

class BootCompletedReceiver : BroadcastReceiver() {

    @Override
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            MainActivity.repeatCheck(context)
            val db = LocalDB.getDB(context)
            val task = db.taskDao().allTask
            for (i in task) {
                if (i.status == 0 && i.calTIM != -1L) {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = i.calTIM
                    setAlarm(i.id, context)
                }
            }
        }
    }
}