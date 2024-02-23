package com.example.todosapp

import android.app.Activity
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
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val CHANNEL_ID = "Task Reminder"
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent


    private val db: LocalDB by lazy { LocalDB.getDB(this) }
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerTaskAdapter
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar
    private lateinit var calendar: Calendar
    private var dateSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolBar = findViewById(R.id.toolBar)

        setSupportActionBar(toolBar)
        supportActionBar?.title = "To Do S App"
        toolBar.subtitle = "Pending task List"


        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAdd)
        showTasks(0)
        btnAdd.setOnClickListener { newTask() }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val backDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                backDialog.setTitle("Close ToDoS App?")
                backDialog.setIcon(R.drawable.baseline_exit_to_app_24)
                backDialog.setMessage("Are you sure you want to exit?")

                backDialog.setPositiveButton("Yes") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                backDialog.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                backDialog.show()
            }

        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)

        createNotChannel()
    }

    override fun onResume() {
        showTasks(0)
        super.onResume()
    }

    fun showTasks(taskStatus: Int) {
        calendar = Calendar.getInstance()
        val arrTask = ArrayList<TaskModel>()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerAdapter = RecyclerTaskAdapter(this, arrTask)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter
        val task = db.taskDao().allTask
        for (i in task) {
            if (i.status == taskStatus) {
                arrTask.add(
                    TaskModel(
                        R.drawable.no_tick_box, i.name, i.description, i.id, i.calTIM
                    )
                )
            }
        }
        if (task.size == 0) {
            db.taskDao().addTask(Task(-1, "name", null, -1, -1))
            db.taskDao().addTask(
                Task(
                    3,
                    "Hey, create your first task by clicking on the plus sign at the bottom right corner!",
                    "And add a description and due date, although they are optional.",
                    0,
                    calendar.timeInMillis
                )
            )
            db.taskDao().addTask(
                Task(
                    4, "Mark your tasks as done or undone by tapping on them!", null, 0, -1
                )
            )
            db.taskDao().addTask(
                Task(
                    5,
                    "You can also edit or delete them with a long press.",
                    "Note that the above task neither has any description, nor any due date.",
                    0,
                    -1
                )
            )
            db.taskDao().addTask(
                Task(
                    6,
                    "Check the tasks you marked as done in the 'Done' section.",
                    "Go to the 'Done' Section by tapping on the tick mark in the top right corner.",
                    0,
                    calendar.timeInMillis
                )
            )
            db.taskDao().addTask(
                Task(
                    1, "Mark a task as Undone by taping on it.", null, 1, calendar.timeInMillis
                )
            )
            db.taskDao().addTask(
                Task(
                    2, "You can delete a task with a long press!", null, 1, calendar.timeInMillis
                )
            )
        }
        val textNoTasks = findViewById<TextView>(R.id.textNoTasks)
        if (arrTask.size == 0) {
            textNoTasks.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            val textNoTaskBut1: String = if (task.size == 1) "Start adding more tasks!"
            else getText(R.string.tasks_complete_text).toString()
            textNoTasks.text = textNoTaskBut1
        } else {
            recyclerView.visibility = View.VISIBLE
            textNoTasks.visibility = View.GONE
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.opt_toolbar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btn_done -> {
                val iNext = Intent(this, DoneTasksActivity::class.java)
                startActivity(iNext)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var txtReminder: TextView
    private lateinit var cal: Calendar

    private fun newTask() {
        calendar = Calendar.getInstance()
        val add = Dialog(this)
        add.setContentView(R.layout.add_edit_task)
        add.show()
        val btnAddDialog = add.findViewById<Button>(R.id.btnAddDialog)
        val edtTaskName = add.findViewById<EditText>(R.id.edtTaskName)
        txtReminder = add.findViewById(R.id.txtReminder)
        val btnSetDue = add.findViewById<Button>(R.id.btn_set_due)
        val edtTaskDescription = add.findViewById<EditText>(R.id.edtTaskDescription)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
            requestFocusNew(edtTaskName)
        } else {
            requestFocus(edtTaskName)
        }


        btnSetDue.setOnClickListener {
            if (Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                    )
                }
            }
            showDatePicker(this, this)
        }

        btnAddDialog.setOnClickListener {
            val text = edtTaskName.text.toString()
            val desc = edtTaskDescription.text.toString()
            val status = 0
            if (text.isBlank()) {
                val toast = Toast.makeText(this, "Task Name is needed!", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                val rem = if (dateSet) {
                    calendar.timeInMillis
                } else {
                    -1
                }
                db.taskDao().addTask(Task(text, desc, status, rem))
                val id = db.taskDao().getTaskId(text, status)
                if (dateSet) {
                    setAlarm(id)
//                    showTaskNotification(this, this, id, calendar)
                    dateSet = false
                }
                calendar = Calendar.getInstance()
                showTasks(0)
                add.dismiss()
            }
        }
    }

    private fun setAlarm(id: Int) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, MyBroadcastReceiver::class.java)
        intent.putExtra("id", id)
        intent.putExtra("taskName", db.taskDao().getTaskName(id).toString())
        pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun createNotChannel() {

//      !! We're not checking for existing channel, but creating new directly !!

        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            val name = "Task Reminder"
//            val desc = "Some description"
            val imp = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, imp)
//                channel.lightColor = Color.GREEN   // what?
                channel.enableVibration(true)
//            channel.description = desc
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
//            }
        }
    }

    private fun showTaskNotification(
        activity: Activity, context: Context, id: Int, notificationCal: Calendar
    ) {
//        Log.d("Time : ", "Now: " + Calendar.getInstance().timeInMillis + " Set: " + notificationCal.timeInMillis)
        val taskName = db.taskDao().getTaskName(id)
        // Schedule the notification
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(context, MainActivity::class.java)
//        val pIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE)
//        val builder =
//            NotificationCompat.Builder(context, CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(taskName).setContentText("Its time for: $taskName")
//                .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pIntent)

//        builder.setContentIntent(pIntent)

//      !! We're not checking for existing channel, but creating new directly !!

//        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
//            val name = "Some NAme"
//            val desc = "Some description"
//            val imp = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, imp)
////                channel.lightColor = Color.GREEN   // what?
////                channel.enableVibration(true)
//            channel.description = desc
//            val notificationManager = getSystemService(NotificationManager::class.java)
//                notificationManager.createNotificationChannel(channel)
////            }
//        }

//        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationCal.timeInMillis, pIntent)


//            notMan.notify(0, builder.build())


        // Show the notification
//        with(NotificationManagerCompat.from(context)) {
//            notify(id, builder.build())
//        }
    }


    private fun showTimePickerDialog(selectedDate: Calendar, activity: Activity, context: Context) {
        val hourOfDay = selectedDate.get(Calendar.HOUR_OF_DAY)
        val minute = selectedDate.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this, { _, selHourOfDay, selMinute ->
                selectedDate.set(Calendar.HOUR_OF_DAY, selHourOfDay)
                selectedDate.set(Calendar.MINUTE, selMinute)
                if (selectedDate.timeInMillis != Calendar.getInstance().timeInMillis) {
                    txtReminder.visibility = View.VISIBLE
                    calendar = selectedDate
                    txtReminder.text = SimpleDateFormat(
                        "h:mm a d/M/yy", Locale.getDefault()
                    ).format((calendar.time))
                    dateSet = true
                }
            }, hourOfDay, minute, false
        )
        timePickerDialog.show()
    }

    private fun showDatePicker(activity: Activity, context: Context) {

        if (txtReminder.text.toString() == "") {
            calendar.time = Date(System.currentTimeMillis())
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dPD = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = calendar
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                // After selecting the date, show the TimePickerDialog
                showTimePickerDialog(selectedDate, activity, context)
            }, year, month, dayOfMonth
        )
        dPD.datePicker.minDate = Calendar.getInstance().timeInMillis
        dPD.show()
    }

    @RequiresApi(VERSION_CODES.R)
    fun requestFocusNew(edtTaskName: EditText) {
        edtTaskName.requestFocus()
        edtTaskName.windowInsetsController?.show(WindowInsetsCompat.Type.ime())
    }

    fun requestFocus(edtTaskName: EditText) {
        edtTaskName.requestFocus()
        // Show the keyboard
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(edtTaskName, InputMethodManager.SHOW_IMPLICIT)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        Log.d("G", "G")
    }
}