package com.example.l3z1

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime

open class AddTaskActivity : AppCompatActivity() {
    
    protected lateinit var name: EditText
    protected lateinit var calendarView: CalendarView
    protected lateinit var description: EditText
    protected lateinit var priority: Spinner
    protected lateinit var hours: Spinner
    protected lateinit var minutes: Spinner
    protected lateinit var adapter:  ArrayAdapter<String>
    protected var chosenPriority : String? = null
    protected var chosenHour : String? = LocalDateTime.now().hour.toString()
    protected var chosenMinutes : String? = LocalDateTime.now().minute.toString()
    protected var chosenImage : String? = null
    @SuppressLint("SimpleDateFormat")
    protected val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        val table = findViewById<TableLayout>(R.id.tableLayout)
        table.layoutParams.height = Resources.getSystem().displayMetrics.widthPixels / 2

        name = findViewById(R.id.name)
        description = findViewById(R.id.description)
        priority = findViewById(R.id.priority)
        hours = findViewById(R.id.hours)
        minutes = findViewById(R.id.minutes)
        calendarView = findViewById(R.id.calendarView)

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val correctMonth = month + 1
            val simpleDateFormat2 = SimpleDateFormat("dd-MM-yyyy")
            val timestamp = simpleDateFormat2.parse("$dayOfMonth-$correctMonth-$year")!!.time
            view.date = timestamp
        }

        val options = arrayOf("Low", "Medium", "High")
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, options)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        priority.adapter = adapter
        priority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenPriority = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val arrayOfHours = arrayOfNulls<String>(24)
        for (i in 0..23) {
            if (i < 10)
                arrayOfHours[i] = "0$i"
            else
                arrayOfHours[i] = i.toString()
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayOfHours)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        hours.adapter = adapter
        hours.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenHour = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val arrayOfMinutes = arrayOfNulls<String>(60)
        for (i in 0..59) {
            if (i < 10)
                arrayOfMinutes[i] = "0$i"
            else
                arrayOfMinutes[i] = i.toString()
        }
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, arrayOfMinutes)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        minutes.adapter = adapter
        minutes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                chosenMinutes = parent?.getItemAtPosition(position).toString()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        hours.setSelection(adapter.getPosition(chosenHour))
        minutes.setSelection(adapter.getPosition(chosenMinutes))

        val images = arrayOf("documents", "economy", "family", "friends", "health", "home", "study", "work")
        for (img in images) {
            var resId = resources.getIdentifier(img, "id", packageName)
            val imageView = findViewById<ImageView>(resId)
            imageView.setOnClickListener {
                if (chosenImage != null) {
                    resId = resources.getIdentifier(chosenImage, "id", packageName)
                    val imgView = findViewById<ImageView>(resId)
                    imgView.setBackgroundColor(Color.parseColor("#ffffff"))
                }
                chosenImage = if (img == chosenImage) {
                    imageView.setBackgroundColor(Color.parseColor("#ffffff"))
                    null
                } else {
                    imageView.setBackgroundColor(Color.parseColor("#ccf2ff"))
                    img
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("chosenImage", chosenImage)
        outState.putLong("time", calendarView.date)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        calendarView.date = savedInstanceState.getLong("time")
        chosenImage = savedInstanceState.getString("chosenImage")
        val resId = resources.getIdentifier(chosenImage, "id", packageName)
        val imageView = findViewById<ImageView>(resId)
        imageView.setBackgroundColor(Color.parseColor("#ccf2ff"))
    }

    open fun onClick(view: View) {
        val newName = name.text.toString()
        if (newName == "") {
            Toast.makeText(view.context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }
        val newDescription = description.text.toString()
        val dateString = simpleDateFormat.format(calendarView.date)
        val date = "$dateString $chosenHour:$chosenMinutes"
        val newTask = Task(newName, newDescription, date, chosenImage, chosenPriority, null)
        GlobalScope.launch {
            DatabaseConnector.insertTask(newTask)
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}