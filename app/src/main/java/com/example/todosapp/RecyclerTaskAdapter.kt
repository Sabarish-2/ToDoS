package com.example.todosapp

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val CHANNEL_ID = "Task Reminder"

class RecyclerTaskAdapter(
    private val context: Context,
    private val arrTask: ArrayList<TaskModel>,
    private val permissionChk: () -> Unit?
) :
    RecyclerView.Adapter<RecyclerTaskAdapter.ViewHolder>(), DatePickerDialog.OnDateSetListener {

    private val db: LocalDB by lazy { LocalDB.getDB(context) }
    private lateinit var calendar: Calendar
    private lateinit var txtReminder: TextView

    private var dateSet = false


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskStatus: ImageView = itemView.findViewById(R.id.im_tick_box)
        val taskName: TextView = itemView.findViewById(R.id.tv_task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.tv_task_description)
        val lLRow: LinearLayout = itemView.findViewById(R.id.llRow)
        val txtRemTR: TextView = itemView.findViewById(R.id.txt_reminder)

        fun showToast(message: String) {
            Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.task_row, parent, false))
    }

    override fun getItemCount(): Int {
        return arrTask.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskStatus.setImageResource(arrTask[position].img)
        holder.taskName.text = arrTask[position].name
        holder.taskDescription.text = arrTask[position].description

        var rep = arrTask[position].rep

        calendar = Calendar.getInstance()
        if (arrTask[position].calTIM.toString() != "-1") {
            calendar.timeInMillis = arrTask[position].calTIM
            holder.txtRemTR.text =
                SimpleDateFormat("h:mm a\nd/M/yy", Locale.getDefault()).format(calendar.time)
        } else holder.txtRemTR.text = ""


        if (holder.taskDescription.text.isNotBlank()) holder.taskDescription.visibility =
            View.VISIBLE
        if (holder.txtRemTR.text.isNotBlank()) holder.txtRemTR.visibility = View.VISIBLE

        val tick = holder.lLRow
        if (context is MainActivity) {
            tick.setOnLongClickListener {
                val edit = Dialog(context)

                edit.setContentView(R.layout.add_edit_task)

                val spRep: Spinner = edit.findViewById(R.id.spRep)
                spRep.setSelection(arrTask[position].rep)
                spRep.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        rep = position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

                val txtDialog = edit.findViewById<TextView>(R.id.txtDialog)
                txtDialog.text = getString(context, R.string.edit_task)

                edit.show()
                val btnAddDialog = edit.findViewById<Button>(R.id.btnAddDialog)
                val btnDelDialog = edit.findViewById<Button>(R.id.btnDelDialog)
                val btnSetDue = edit.findViewById<Button>(R.id.btn_set_due)
                val edtTaskName = edit.findViewById<EditText>(R.id.edtTaskName)
                val edtTaskDescription = edit.findViewById<EditText>(R.id.edtTaskDescription)
                txtReminder = edit.findViewById(R.id.txtReminder)


                if (arrTask[position].calTIM.toString() != "-1") {
                    calendar.timeInMillis = arrTask[position].calTIM
                    txtReminder.text = SimpleDateFormat(
                        "h:mm a d/M/yy",
                        Locale.getDefault()
                    ).format((calendar.time))
                    txtReminder.visibility = View.VISIBLE
                }
                edtTaskName.setText(holder.taskName.text)
                edtTaskDescription.setText(holder.taskDescription.text)
                btnAddDialog.text = getString(context, R.string.btn_done)
                btnDelDialog.visibility = View.VISIBLE

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.requestFocusNew(edtTaskName)
                } else {
                    context.requestFocus(edtTaskName)
                }

                btnSetDue.setOnClickListener {
                    if (!permissionChk.equals(null))
                        permissionChk.invoke()
                    if (ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        Toast.makeText(
                            context,
                            "Notification Permission Required to show Reminder!",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setOnClickListener
                    }

                    showDatePicker(context)
                    if (arrTask[position].calTIM.toString() != "-1") calendar.timeInMillis =
                        arrTask[position].calTIM
                    dateSet = true
                    createNotChannel()
                }
                btnDelDialog.setOnClickListener {
                    val delDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                    delDialog.setTitle("Delete Task?")
                    delDialog.setIcon(R.drawable.baseline_delete_24)
                    delDialog.setMessage("Are you sure to Delete\n" + arrTask[position].name + " Task?")
                    delDialog.setPositiveButton("Yes") { dialog, _ ->
                        val new = Task(
                            arrTask[position].id,
                            arrTask[position].name,
                            arrTask[position].description,
                            arrTask[position].img,
                            arrTask[position].calTIM,
                            arrTask[position].rep,
                            arrTask[position].freq
                        )

                        deleteAlarm(arrTask[position].id, context)
                        db.taskDao().delTask(new)
                        dialog.dismiss()
                        edit.dismiss()
                        (context).showTasks(0)
                    }
                    delDialog.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    delDialog.show()
                }
                btnAddDialog.setOnClickListener {
                    val text = edtTaskName.text.toString()
                    val description = edtTaskDescription.text.toString()
                    if (!dateSet) calendar.timeInMillis = arrTask[position].calTIM
                    if (text.isBlank()) {
                        holder.showToast("Task Name is Needed!")
                    } else {
                        val freq = if (dateSet) {
                            -((calendar.timeInMillis - Calendar.getInstance().timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                        } else {
                            0
                        }
                        val new =
                            Task(
                                arrTask[position].id,
                                text,
                                description,
                                0,
                                calendar.timeInMillis,
                                rep,
                                freq
                            )
                        db.taskDao().editTask(new)
                        (context).showTasks(0)
                        deleteAlarm(arrTask[position].id, context)
                        setAlarm(arrTask[position].id, context)
                    }
                    dateSet = false
                    edit.dismiss()
                }
                true
            }
        } else if (context is DoneTasksActivity) {
            tick.setOnLongClickListener {

                val delDialog: AlertDialog.Builder = AlertDialog.Builder(context)
                delDialog.setTitle("Delete Task?")
                delDialog.setIcon(R.drawable.baseline_delete_24)
                delDialog.setMessage("Are you sure to Delete\n" + arrTask[position].name + " Task?")

                delDialog.setPositiveButton("Yes") { dialog, _ ->
                    val new = Task(
                        arrTask[position].id,
                        arrTask[position].name,
                        arrTask[position].description,
                        arrTask[position].img,
                        arrTask[position].calTIM
                    )
                    deleteAlarm(arrTask[position].id, context)
                    db.taskDao().delTask(new)
                    dialog.dismiss()
                    (context).showTasks(1)
                }
                delDialog.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                delDialog.show()

                true
            }
        }

        tick.setOnClickListener {
            if (context is MainActivity) {
                val new = Task(
                    arrTask[position].id,
                    arrTask[position].name,
                    arrTask[position].description,
                    1,
                    arrTask[position].calTIM,
                    arrTask[position].rep,
                    arrTask[position].freq
                )
                deleteAlarm(arrTask[position].id, context)
                db.taskDao().editTask(new)
                (context).showTasks(0)
            } else {
                val new = Task(/* id = */ arrTask[position].id,
                    arrTask[position].name,
                    arrTask[position].description,
                    0,
                    arrTask[position].calTIM,
                    arrTask[position].rep,
                    arrTask[position].freq
                )
                val calTIM = arrTask[position].calTIM
                if (calTIM > Calendar.getInstance().timeInMillis)
                    setAlarm(arrTask[position].id, context)
                db.taskDao().editTask(new)
                (context as DoneTasksActivity).showTasks(1)
            }
        }
    }

    private fun showDatePicker(context: Context) {

        if (txtReminder.text.toString() == "") calendar.time = Date(System.currentTimeMillis())

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dPD = DatePickerDialog(
            context, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = calendar
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                // After selecting the date, show the TimePickerDialog
                showTimePickerDialog(selectedDate)
            }, year, month, dayOfMonth
        )
        dPD.datePicker.minDate = Calendar.getInstance().timeInMillis
        dPD.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {}

    private fun createNotChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminder"
            val desc =
                "This is the notification for the a task's reminder that you can set by holding a task in Pending tasks screen!"
            val imp = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, imp)
            channel.lightColor = Color.GREEN   // what?
            channel.enableVibration(true)
            channel.description = desc
            getSystemService(context, NotificationManager::class.java)?.createNotificationChannel(
                channel
            )
        }
    }

    private fun showTimePickerDialog(selectedDate: Calendar) {
        val hourOfDay = selectedDate.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDate.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context, { _, selHourOfDay, selMinute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, selHourOfDay)
                selectedDate.set(Calendar.MINUTE, selMinute)
                if (selectedDate.timeInMillis != Calendar.getInstance().timeInMillis) {
                    txtReminder.visibility = View.VISIBLE
                    calendar = selectedDate
                    txtReminder.text = SimpleDateFormat(
                        "h:mm a d/M/yy",
                        Locale.getDefault()
                    ).format((calendar.time))
                    dateSet = true
                }
            }, hourOfDay, minute, false
        )
        timePickerDialog.show()
    }

    init {
        arrTaskStatic = arrTask
    }

    companion object {

        private lateinit var alarmManager: AlarmManager
        private lateinit var pendingIntent: PendingIntent
        private lateinit var calendar: Calendar
        private lateinit var arrTaskStatic: ArrayList<TaskModel>


        fun deleteAlarm(id: Int, context: Context) {
            alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyBroadcastReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }

        fun setAlarm(id: Int, context: Context) {
            val db = LocalDB.getDB(context)
            calendar = Calendar.getInstance()
            val task = db.taskDao().getTask(id)
            calendar.timeInMillis = task.calTIM
            if (calendar.timeInMillis < Calendar.getInstance().timeInMillis) {
                deleteAlarm(id, context)
                return
            }

            alarmManager = context.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, MyBroadcastReceiver::class.java)
            intent.putExtra("id", id)
            intent.putExtra("taskName", db.taskDao().getTaskName(id).toString())
            pendingIntent = PendingIntent.getBroadcast(
                context,
                id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
}
