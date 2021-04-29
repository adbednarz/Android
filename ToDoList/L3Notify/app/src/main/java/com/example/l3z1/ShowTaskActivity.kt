package com.example.l3z1

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ShowTaskActivity : AppCompatActivity() {

    private var position : Int = 0
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_task)
        position = intent.getIntExtra("index", 0)
        val name = findViewById<TextView>(R.id.name)
        val date = findViewById<TextView>(R.id.date)
        val priority = findViewById<TextView>(R.id.priority)
        val description = findViewById<TextView>(R.id.description)
        val image = findViewById<ImageView>(R.id.imageView)
        GlobalScope.launch {
            task = DatabaseConnector.getSpecific(position)
            name.text = task?.name
            val year = task?.date?.take(4)
            val month = task?.date?.substring(5, 7)
            val day = task?.date?.substring(8, 10)
            var tmp = task?.date?.substring(11)
            tmp += " $day.$month.$year"
            date.text = tmp
            priority.text = task?.priority
            description.text = task?.description
            if (task?.icon != null) {
                val resId: Int = resources.getIdentifier(task?.icon, "drawable", packageName)
                image.setImageResource(resId)
            }
        }
    }

    fun onClick(view: View) {
        val intent = Intent(this, EditTaskActivity::class.java)
        intent.putExtra("index", position)
        view.context.startActivity(intent)
    }
}