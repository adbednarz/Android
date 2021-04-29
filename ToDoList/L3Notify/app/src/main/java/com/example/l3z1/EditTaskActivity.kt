package com.example.l3z1

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditTaskActivity : AddTaskActivity() {

    private var position: Int = 0
    private var task: Task? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val button = findViewById<Button>(R.id.button)
        button.text = "Change"

        position = intent.getIntExtra("index", 0)
        GlobalScope.launch {
            task = DatabaseConnector.getSpecific(position)
            name.setText(task?.name)
            priority.setSelection(adapter.getPosition(task?.priority))
            description.setText(task?.description)
            chosenHour = task?.date?.substring(11, 13)
            chosenMinutes = task?.date?.substring(14)
            hours.setSelection(adapter.getPosition(chosenHour))
            minutes.setSelection(adapter.getPosition(chosenMinutes))
            val timestamp = simpleDateFormat.parse(task?.date!!.take(10))!!.time
            if (savedInstanceState == null) {
                calendarView.date = timestamp
                chosenImage = task?.icon
                val images = arrayOf<String>("documents", "economy", "family", "friends", "health", "home", "study", "work")
                for (img in images) {
                    val resId = resources.getIdentifier(img, "id", packageName)
                    val imageView = findViewById<ImageView>(resId)
                    if (chosenImage == img)
                        imageView.setBackgroundColor(Color.parseColor("#ccf2ff"))
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (name.text.isEmpty()) {
            Toast.makeText(view.context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val dateString = simpleDateFormat.format(calendarView.date)
        val date = "$dateString $chosenHour:$chosenMinutes"
        task?.name = name.text.toString()
        task?.description = description.text.toString()
        task?.date = date
        task?.icon = chosenImage
        task?.priority = chosenPriority
        GlobalScope.launch {
            DatabaseConnector.updateTask(task)
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}