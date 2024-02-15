package com.example.todosapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
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
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private val db: LocalDB by lazy {
        LocalDB.getDB(this)
    }
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


        val btnAdd =
            findViewById<FloatingActionButton>(R.id.btnAdd)
        showTasks(0)
        btnAdd.setOnClickListener {
            newTask()
        }

        val onBackPressedCallback = object:OnBackPressedCallback(true){
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
                        R.drawable.no_tick_box,
                        i.name,
                        i.description,
                        i.id,
                        i.dueDate
                    )
                )
            }
        }
        if (task.size == 0) {
            db.taskDao().addTask(Task(-1, "name", null, -1, Date(-1)))
            db.taskDao().addTask(
                Task(
                    3,
                    "Hey, create your first task by clicking on the plus sign at the bottom right corner!",
                    "And add a description and due date, although they are optional.",
                    0,
                    Date(calendar.timeInMillis)
                )
            )
            db.taskDao().addTask(
                Task(
                    4, "Mark your tasks as done or undone by tapping on them!", null, 0, Date(-1)
                )
            )
            db.taskDao().addTask(
                Task(
                    5,
                    "You can also edit or delete them with a long press.",
                    "Note that the above task neither has any description, nor any due date.",
                    0, Date(-1)
                )
            )
            db.taskDao().addTask(
                Task(
                    6,
                    "Check the tasks you marked as done in the 'Done' section.",
                    "Go to the 'Done' Section by tapping on the tick mark in the top right corner.",
                    0, Date(calendar.timeInMillis)
                )
            )
            db.taskDao().addTask(
                Task(
                    1,
                    "Mark a task as Undone by taping on it.",
                    null,
                    1, Date(calendar.timeInMillis)
                )
            )
            db.taskDao().addTask(
                Task(
                    2,
                    "You can delete a task with a long press!",
                    null,
                    1, Date(calendar.timeInMillis)
                )
            )
        }
        val textNoTasks = findViewById<TextView>(R.id.textNoTasks)
        if (arrTask.size == 0) {
            textNoTasks.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            val textNoTaskBut1: String = if (task.size == 1)
                "Start adding more tasks!"
            else
                getText(R.string.tasks_complete_text).toString()
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

    private lateinit var txtDueDate: TextView
    private lateinit var date: Date

    private fun newTask() {
        calendar = Calendar.getInstance()
        val add = Dialog(this)
        add.setContentView(R.layout.add_edit_task)
        add.show()
        val btnAddDialog = add.findViewById<Button>(R.id.btnAddDialog)
        val edtTaskName = add.findViewById<EditText>(R.id.edtTaskName)
        txtDueDate = add.findViewById(R.id.txtReminder)
        val btnSetDue = add.findViewById<Button>(R.id.btn_set_due)
        val edtTaskDescription = add.findViewById<EditText>(R.id.edtTaskDescription)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
            requestFocusNew(edtTaskName)
        } else {
            requestFocus(edtTaskName)
        }


        btnSetDue.setOnClickListener {
            showDatePicker()
            if (dateSet) {
                date =
                    if (calendar.timeInMillis != 0L) Date(calendar.timeInMillis) else Date(-1L)
                txtDueDate.text = DateFormat.getDateInstance().format(date)
                dateSet = false
            }
        }

        btnAddDialog.setOnClickListener {
            val text = edtTaskName.text.toString()
            val desc = edtTaskDescription.text.toString()
            val status = 0
            if (text.isBlank()) {
                val toast = Toast.makeText(this, "Task Name is needed!", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                if (dateSet) {
                    date =
                        if (calendar.timeInMillis != 0L) Date(calendar.timeInMillis) else Date(-1L)
                    dateSet = false
                } else date = Date(-1)
                db.taskDao().addTask(Task(text, desc, status, date))
                calendar = Calendar.getInstance()
                showTasks(0)
                add.dismiss()
            }
        }
    }

    private fun showDatePicker() {
        calendar = Calendar.getInstance()

        if (txtDueDate.text.toString() != "") {
            calendar.time = DateFormat.getDateInstance().parse(txtDueDate.text.toString())!!
        } else {
            calendar.time = Date(System.currentTimeMillis())
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dPD = DatePickerDialog(this, this, year, month, dayOfMonth)
        dPD.datePicker.minDate = Calendar.getInstance().timeInMillis
        dPD.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        txtDueDate.visibility = View.VISIBLE
        txtDueDate.text = DateFormat.getDateInstance().format(Date(calendar.timeInMillis))
        dateSet = true
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
}