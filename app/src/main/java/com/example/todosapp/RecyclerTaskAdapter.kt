package com.example.todosapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RecyclerTaskAdapter(private val context: Context, private val arrTask: ArrayList<TaskModel>) :
    RecyclerView.Adapter<RecyclerTaskAdapter.ViewHolder>(), DatePickerDialog.OnDateSetListener {

    private val db: LocalDB by lazy {
        LocalDB.getDB(context)
    }
    private lateinit var calendar: Calendar
    private lateinit var dueDate: Date
    private lateinit var txtDueDate: TextView

    private var dateSet = false
    private val outputFormat = SimpleDateFormat("d/M/yy", Locale.getDefault())

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val taskStatus: ImageView = itemView.findViewById(R.id.im_tick_box)
        val taskName: TextView = itemView.findViewById(R.id.tv_task_name)
        val taskDescription: TextView = itemView.findViewById(R.id.tv_task_description)
        val lLRow: LinearLayout = itemView.findViewById(R.id.llRow)
        val dueDate: TextView = itemView.findViewById(R.id.txt_due_date)

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

        calendar = Calendar.getInstance()
        if(arrTask[position].dueDate!!.time.toString() != "-1")
        {
            holder.dueDate.text = outputFormat.format((arrTask[position].dueDate!!))
            calendar.time = arrTask[position].dueDate!!
        }
        else
            holder.dueDate.text = ""


        if (holder.taskDescription.text.isNotBlank())
            holder.taskDescription.visibility = View.VISIBLE
        if (holder.dueDate.text.isNotBlank())
            holder.dueDate.visibility = View.VISIBLE

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
                val btnSetDue = edit.findViewById<Button>(R.id.btn_set_due)
                val edtTaskName = edit.findViewById<EditText>(R.id.edtTaskName)
                val edtTaskDescription = edit.findViewById<EditText>(R.id.edtTaskDescription)
                txtDueDate = edit.findViewById(R.id.txtReminder)


                if(arrTask[position].dueDate!!.time.toString() != "-1") {
                    txtDueDate.text = DateFormat.getDateInstance().format(arrTask[position].dueDate!!)
                    txtDueDate.visibility = View.VISIBLE
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
                    showDatePicker(context)
                    if (arrTask[position].dueDate!!.time.toString() != "-1")
                        calendar.time = arrTask[position].dueDate!!
                    dateSet = true
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
                            arrTask[position].dueDate
                        )
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
                    dueDate = if (dateSet)
                        Date(calendar.timeInMillis)
                    else
                        arrTask[position].dueDate!!
                    if (text.isBlank()) {
                        holder.showToast("Task Name is Needed!")
                    } else {
                        val new = Task(arrTask[position].id, text, description, 0, dueDate)
                        db.taskDao().editTask(new)
                        (context).showTasks(0)
                    }
                    dateSet = false
                    edit.dismiss()
                }
                true
            }
        } else if (context is DoneTasksActivity){
            tick.setOnLongClickListener{

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
                        arrTask[position].dueDate
                    )
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
                    /* id = */ arrTask[position].id,
                    /* name = */ arrTask[position].name,
                    /* description = */ arrTask[position].description,
                    /* status = */ 1,
                    arrTask[position].dueDate
                )
                db.taskDao().editTask(new)
                (context).showTasks(0)
            } else {
                val new = Task(
                    /* id = */ arrTask[position].id,
                    /* name = */ arrTask[position].name,
                    /* description = */ arrTask[position].description,
                    /* status = */ 0,
                    arrTask[position].dueDate
                )
                db.taskDao().editTask(new)
                (context as DoneTasksActivity).showTasks(1)
            }
        }
    }

    private fun showDatePicker(context: Context) {
        if (txtDueDate.text.toString() != "") {
            calendar.time = DateFormat.getDateInstance().parse(txtDueDate.text.toString())!!
        }
        else {
            calendar.time = Date(System.currentTimeMillis())
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val dPD = DatePickerDialog(context, this, year, month, dayOfMonth)
        dPD.datePicker.minDate = Calendar.getInstance().timeInMillis
        dPD.show()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(year, month, dayOfMonth)
        txtDueDate.visibility = View.VISIBLE
        txtDueDate.text = DateFormat.getDateInstance().format(Date(calendar.timeInMillis))
        dateSet = true
    }
}