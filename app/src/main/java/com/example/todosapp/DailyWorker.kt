package com.example.todosapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

class DailyWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val db: LocalDB = LocalDB.getDB(applicationContext)
        val taskList = db.taskDao().allTask.toMutableList()
        for (i in taskList)
        {
            i.freq++
            if (i.rep == 1 || (i.rep == 2 && i.freq % 7 == 0) || (i.rep == 3 && i.freq % 30 == 0))
            {
                i.status = 0
                i.freq = 0
                if (i.calTIM.toString() != "-1")
                {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = i.calTIM
                    calendar.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                    calendar.set(Calendar.MONTH, Calendar.getInstance().get(Calendar.MONTH))
                    calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
                    i.calTIM = calendar.timeInMillis
                    db.taskDao().editTask(i)
                    RecyclerTaskAdapter.setAlarm(i.id, applicationContext)
                }
            }
            else db.taskDao().editTask(i)
        }
        return Result.success()
    }

}