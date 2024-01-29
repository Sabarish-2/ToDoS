package com.example.todosapp

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setMargins

class DoneTasksActivity : AppCompatActivity() {

    private var tasks = 0
    private val db: LocalDB by lazy {
        LocalDB.getDB(this)
    }

    private lateinit var toolBar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done_tasks)
        toolBar = findViewById(R.id.toolBar)

        setSupportActionBar(toolBar)
//        Back Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Your Task App"
        toolBar.subtitle = "Completed task List"
        val task = db.taskDao().allTask
        for (i in task) {
            tasks++
            if (i.status == 1) {
                checkBox(i.name)
            }
            noTaskChecker()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun checkBox(taskName1: String) {
        var taskName = taskName1
        val cbLayout = findViewById<LinearLayout>(R.id.cbLayout)
        val checkBox = CheckBox(this)
        checkBox.text = taskName
        checkBox.isChecked = true
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(24)
        checkBox.layoutParams = layoutParams
        checkBox.setOnClickListener {
            if (!checkBox.isChecked) {
                val id = if (checkBox.id > 0) checkBox.id else db.taskDao().getTaskId(taskName, 1)
                val new = Task(id, taskName, 0)
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestFocusNew(edtTaskName)
            } else {
                requestFocus(edtTaskName)
            }
            btnDelDialog.setOnClickListener {
                val id = if (checkBox.id > 0) checkBox.id else (db.taskDao().getTaskId(taskName, 1))
                val new = Task(id, taskName, 1)
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
                    val id =
                        if (checkBox.id > 0) checkBox.id else (db.taskDao().getTaskId(taskName, 1))
                    val new = Task(id, text, 1)
                    db.taskDao().editTask(new)
                    taskName = text
                }
                add.dismiss()
            }
            true
        }
        cbLayout?.addView(checkBox)
        noTaskChecker()
    }

    private fun removeTaskCheck(cbLayout: LinearLayout?, newCheckBox: CheckBox) {
        cbLayout?.removeView(newCheckBox)
        tasks--
        noTaskChecker()
    }

    private fun noTaskChecker() {
        val textNoTasks = findViewById<TextView>(R.id.textNoTasks)
        if (tasks == 0) textNoTasks.visibility = View.VISIBLE
        else textNoTasks.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.R)
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

}

