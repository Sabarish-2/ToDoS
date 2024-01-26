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

    private var tasks = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textNoTasks = findViewById<TextView>(R.id.textNoTasks)
        val btnAdd =
            findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.btnAdd)
        val cbLayout = findViewById<LinearLayout>(R.id.cbLayout)

        noTaskChecker(textNoTasks)


        btnAdd.setOnClickListener {
            newTask(cbLayout, textNoTasks)
            noTaskChecker(textNoTasks)
//            taskCheckedChecker(cbLayout, textNoTasks)
        }
    }

    private fun newTask(cbLayout: LinearLayout?, textNoTasks: TextView) {

        val add = Dialog(this)
        add.setContentView(R.layout.add_edit_task)
        add.show()
        val btnAddDialog = add.findViewById<Button>(R.id.btnAddDialog)
        val edtTaskName = add.findViewById<EditText>(R.id.edtTaskName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestFocusNew(edtTaskName)
        } else {
            requestFocus(edtTaskName)
        }
        btnAddDialog.setOnClickListener {
            val text = edtTaskName.text.toString()
            val newCheckBox = CheckBox(this)
            newCheckBox.text = text
            newCheckBox.id = View.generateViewId()
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(8)
            newCheckBox.layoutParams = layoutParams
            newCheckBox.setOnClickListener{
                if (newCheckBox.isChecked)
                    cbLayout?.removeView(newCheckBox)
                    tasks--
                    noTaskChecker(textNoTasks)
            }
            if (text.isBlank()) {
                add.dismiss()
                val toast = Toast.makeText(this, "Task Name is needed!", Toast.LENGTH_SHORT)
                toast.show()
            }
            else{
                tasks++
                cbLayout?.addView(newCheckBox)
                noTaskChecker(textNoTasks)
            }
            add.dismiss()
        }
    }

    @RequiresApi(VERSION_CODES.R)
    private fun requestFocusNew(edtTaskName: EditText) {
        edtTaskName.windowInsetsController?.show(WindowInsetsCompat.Type.ime())
    }
    private fun requestFocus(edtTaskName: EditText) {
        edtTaskName.requestFocus()
        // Show the keyboard
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(edtTaskName, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun noTaskChecker(textNoTasks: TextView) {
        if (tasks == 0) textNoTasks.visibility = View.VISIBLE
        else textNoTasks.visibility = View.INVISIBLE
    }
}