package com.example.todosapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DoneTasksActivity : AppCompatActivity() {

    private val db: LocalDB by lazy {
        LocalDB.getDB(this)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: RecyclerTaskAdapter
    private lateinit var toolBar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done_tasks)
        toolBar = findViewById(R.id.toolBar)

        setSupportActionBar(toolBar)
//      Back Button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "To Do S App"
        toolBar.subtitle = "Completed task List"
        showTasks(1)
    }

    fun showTasks(taskStatus: Int) {
        val arrTask = ArrayList<TaskModel>()
        recyclerView = findViewById(R.id.recyclerView)
        recyclerAdapter = RecyclerTaskAdapter(this, arrTask) { }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerAdapter
        val task = db.taskDao().allTask
        for (i in task) {
            if (i.status == taskStatus) {
                arrTask.add(TaskModel(R.drawable.tick_box, i.name, i.description, i.id, i.calTIM, i.rep, i.freq))
            }
        }
        val textNoTasks = findViewById<TextView>(R.id.textNoTasks)
        if (arrTask.size == 0) {
            textNoTasks.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            val textNoTaskBut1 : String = if (task.size == 1)
                "Start Adding More Tasks!"
            else
                getText(R.string.txt_task_incomplete).toString()
            textNoTasks.text = textNoTaskBut1
        } else {
            recyclerView.visibility = View.VISIBLE
            textNoTasks.visibility = View.GONE
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
}

