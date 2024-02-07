package com.example.todosapp

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView

class RecyclerTaskAdapter(private val context: Context, private val arrTask: ArrayList<TaskModel>) :
    RecyclerView.Adapter<RecyclerTaskAdapter.ViewHolder>() {

    private val db: LocalDB by lazy {
        LocalDB.getDB(context)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskStatus: ImageView = itemView.findViewById(R.id.im_tick_box)
        val taskName: TextView = itemView.findViewById(R.id.tv_task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.tv_task_description)
        val lLRow: LinearLayout = itemView.findViewById(R.id.llRow)
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
        if (holder.taskDescription.text.isNotBlank())
            holder.taskDescription.visibility = View.VISIBLE

        val tick = holder.lLRow
        if (context is MainActivity) {
            tick.setOnLongClickListener {
                val edit = Dialog(context)

                edit.setContentView(R.layout.add_edit_task)

                val txtDialog = edit.findViewById<TextView>(R.id.txtDialog)
                txtDialog.text = getString(context, R.string.edit_task)

                edit.show()
                val btnAddDialog = edit.findViewById<Button>(R.id.btnAddDialog)
                val btnDelDialog = edit.findViewById<Button>(R.id.btnDelDialog)
                val edtTaskName = edit.findViewById<EditText>(R.id.edtTaskName)
                val edtTaskDescription = edit.findViewById<EditText>(R.id.edtTaskDescription)
                edtTaskName.setText(holder.taskName.text)
                edtTaskDescription.setText(holder.taskDescription.text)
                btnAddDialog.text = getString(context, R.string.btn_done)
                btnDelDialog.visibility = View.VISIBLE
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    context.requestFocusNew(edtTaskName)
                } else {
                    context.requestFocus(edtTaskName)
                }
                btnDelDialog.setOnClickListener {
                    val new = Task(
                        arrTask[position].id,
                        arrTask[position].name,
                        arrTask[position].description,
                        arrTask[position].img
                    )
                    db.taskDao().delTask(new)
                    edit.dismiss()
                    (context).showTasks(0)
                }
                btnAddDialog.setOnClickListener {
                    val text = edtTaskName.text.toString()
                    val description = edtTaskDescription.text.toString()
                    if (text.isBlank()) {
                        holder.showToast("Task Name is Needed!")
                    } else {
                        val new = Task(arrTask[position].id, text, description, 0)
                        db.taskDao().editTask(new)
                        (context).showTasks(0)
                    }
                    edit.dismiss()
                }
                true
            }
        }
        tick.setOnClickListener {
            if (context is MainActivity) {
                val new = Task(
                    /* id = */ arrTask[position].id,
                    /* name = */ arrTask[position].name,
                    /* description = */ arrTask[position].description,
                    /* status = */ 1
                )
                db.taskDao().editTask(new)
                (context).showTasks(0)
            } else {
                val new = Task(
                    /* id = */ arrTask[position].id,
                    /* name = */ arrTask[position].name,
                    /* description = */ arrTask[position].description,
                    /* status = */ 0
                )
                db.taskDao().editTask(new)
                (context as DoneTasksActivity).showTasks(1)
            }
        }
    }

}