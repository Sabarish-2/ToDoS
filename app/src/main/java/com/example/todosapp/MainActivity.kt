package com.example.todosapp

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setMargins

class MainActivity : AppCompatActivity() {

    private var tasks: Int = 0
    private val db :LocalDB by lazy {
         LocalDB.getDB(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnAdd =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAdd)
        val task = db.taskDao().allTask
        for (i in task) {
            if (i.status == 0) {
                tasks++
                checkBox(i.name, false)
            }
        }
        if (task.isEmpty()) {
            checkBox(
                "Hey, Create Your First task By Clicking on the Plus Below!",
                true
            )
            checkBox(
                "Mark Your Tasks as Done by marking them tick!!",
                true
            )
            checkBox("Also Edit them with a Long Press!!!", true)
        }
        noTaskChecker()
        btnAdd.setOnClickListener {
            newTask()
            noTaskChecker()
        }
    }

    private fun newTask() {

        val add = Dialog(this)
        add.setContentView(R.layout.add_edit_task)
        add.show()
        val btnAddDialog = add.findViewById<Button>(R.id.btnAddDialog)
        val edtTaskName = add.findViewById<EditText>(R.id.edtTaskName)
        if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
            requestFocusNew(edtTaskName)
        } else {
            requestFocus(edtTaskName)
        }
        btnAddDialog.setOnClickListener {
            val text = edtTaskName.text.toString()
            if (text.isBlank()) {
                add.dismiss()
                val toast = Toast.makeText(this, "Task Name is needed!", Toast.LENGTH_SHORT)
                toast.show()
            } else {
                checkBox(edtTaskName.text.toString(), true)
            }
            add.dismiss()
        }
    }

    private fun checkBox(
        taskNameOld: String,
        newTask: Boolean
    ) {
        var taskName = taskNameOld
        val cbLayout = findViewById<LinearLayout>(R.id.cbLayout)
        val checkBox = CheckBox(this)
        checkBox.text = taskName
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(12)
        checkBox.layoutParams = layoutParams
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
//                db.doneTask(taskName)
                val id = if (checkBox.id > 0) checkBox.id else db.taskDao().getTaskId(taskName, 0)
                val new = Task(id, taskName, 1)
                db.taskDao().editTask(new)
                removeTaskCheck(cbLayout, checkBox)
            }
        }
        checkBox.setOnLongClickListener {
            val add = Dialog(this)
            add.setContentView(R.layout.add_edit_task)

            val txtDialog = add.findViewById<TextView>(R.id.txtDialog)
            txtDialog.text = getString(R.string.edit_task)

            add.show()
            val btnAddDialog = add.findViewById<Button>(R.id.btnAddDialog)
            val btnDelDialog = add.findViewById<Button>(R.id.btnDelDialog)
            val edtTaskName = add.findViewById<EditText>(R.id.edtTaskName)
            edtTaskName.setText(taskName)
            btnAddDialog.text = getString(R.string.btn_set_name)
            btnDelDialog.visibility = View.VISIBLE
            if (Build.VERSION.SDK_INT >= VERSION_CODES.R) {
                requestFocusNew(edtTaskName)
            } else {
                requestFocus(edtTaskName)
            }
            btnDelDialog.setOnClickListener {
//                db.delTask(taskName, 0)
                val id = if (checkBox.id > 0) checkBox.id else (db.taskDao().getTaskId(taskName, 0))
                val new = Task(id, taskName, 0)
                db.taskDao().delTask(new)
                removeTaskCheck(cbLayout, checkBox)
                add.dismiss()
            }
            btnAddDialog.setOnClickListener {
                val text = edtTaskName.text.toString()
                if (text.isBlank()) {
                    val toast = Toast.makeText(this, "Task Name is Needed!", Toast.LENGTH_SHORT)
                    toast.show()
                } else {
                    checkBox.text = text
                    val id = if (checkBox.id > 0) checkBox.id else (db.taskDao().getTaskId(taskName, 0))
                    val new = Task(id, text,0)
                    db.taskDao().editTask(new)
                    taskName = text
                }
                add.dismiss()
            }
            true
        }

        if (newTask) {
            tasks++
            val new = Task(taskName, 0)
            db.taskDao().addTask(new)
            checkBox.id = db.taskDao().getTaskId(taskName, 0)
//            checkBox.id = db.addTask(taskName)
        }
        cbLayout?.addView(checkBox)
        noTaskChecker()
    }

    private fun removeTaskCheck(cbLayout: LinearLayout?, newCheckBox: CheckBox) {
        cbLayout?.removeView(newCheckBox)
        tasks--
        noTaskChecker()
    }

    @RequiresApi(VERSION_CODES.R)
    private fun requestFocusNew(edtTaskName: EditText) {
        edtTaskName.requestFocus()
        edtTaskName.windowInsetsController?.show(WindowInsetsCompat.Type.ime())
    }

    private fun requestFocus(edtTaskName: EditText) {
        edtTaskName.requestFocus()
        // Show the keyboard
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(edtTaskName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun noTaskChecker() {
        val textNoTasks = findViewById<TextView>(R.id.textNoTasks)
        if (tasks == 0) textNoTasks.visibility = View.VISIBLE
        else textNoTasks.visibility = View.GONE
    }
}
