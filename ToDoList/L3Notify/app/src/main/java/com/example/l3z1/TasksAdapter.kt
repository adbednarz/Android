package com.example.l3z1

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TableRow
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TasksAdapter(private val data: ArrayList<Task>) : RecyclerView.Adapter<TasksAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val icon: ImageView = view.findViewById(R.id.icon)
        val description: TextView = view.findViewById(R.id.description)
        val priority: TextView = view.findViewById(R.id.priority)
        val date: TextView = view.findViewById(R.id.date)
        val tableRow: TableRow = view.findViewById(R.id.tableRow)

        init {
            view.setOnClickListener {
                val intent = Intent(view.context, ShowTaskActivity::class.java)
                intent.putExtra("index", data[layoutPosition].addingOrder)
                view.context.startActivity(intent)
            }
            view.setOnLongClickListener {
                tableRow.setBackgroundColor(Color.parseColor("#add8e6"))
                val builder = AlertDialog.Builder(view.context)
                builder.setTitle("WARNING")
                builder.setMessage("Are you sure you want to delete this task?")
                builder.setPositiveButton("Yes") { _, _ ->
                    val tmp = data[layoutPosition]
                    GlobalScope.launch {
                        DatabaseConnector.deleteTask(tmp)
                    }
                    data.removeAt(layoutPosition)
                    tableRow.setBackgroundColor(Color.parseColor("#ffffff"))
                    notifyDataSetChanged()
                }
                builder.setNegativeButton("No") {_, _ ->
                    tableRow.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                builder.setOnCancelListener {
                    tableRow.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                builder.show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tasks_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tableRow.layoutParams.height = Resources.getSystem().displayMetrics.widthPixels / 6
        holder.title.text = data[position].name
        if (data[position].icon != null) {
            val resId: Int = holder.view.resources.getIdentifier(data[position].icon, "drawable", holder.view.context.packageName)
            holder.icon.setImageResource(resId)
        } else {
            holder.icon.setImageResource(android.R.color.transparent)
        }
        var str = data[position].description as String
        if (str.length > 10)
            str = str.take(10) + "..."
        holder.description.text = str
        holder.priority.text = data[position].priority
        str = data[position].date?.take(10) as String
        val year = str.take(4)
        val month = str.substring(5, 7)
        val day = str.substring(8, 10)
        var tmp = data[position].date?.substring(11)
        tmp += " $day.$month.$year"
        holder.date.text = tmp
    }

    override fun getItemCount(): Int {
        return data.size
    }
}