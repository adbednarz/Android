package com.example.l3z1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.getSystemService
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.l3z1.Notify.Companion.createNotify
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private var tasksList = ArrayList<Task>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var sort: MenuItem
    private lateinit var db: DatabaseConnector
    private lateinit var taskDao: TaskDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { _ ->
            val intent = Intent(this, AddTaskActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recycleView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        DatabaseConnector.getDatabaseConnector(this)

        if (this::sort.isInitialized)   // sprawdza czy zmienna została zainicjalizowana
            onOptionsItemSelected(sort)
        else {
            GlobalScope.launch {
                tasksList = DatabaseConnector.getAll() as ArrayList<Task>
                runOnUiThread {
                    recyclerView.adapter = TasksAdapter(tasksList)
                }
            }
        }

        createNotify(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        sort = item
        GlobalScope.launch {
            val tasks = DatabaseConnector.getSorted(item)
            if (tasks != null) {
                for (i in tasks.indices) {
                    tasksList[i] = tasks[i]
                }
            }
            runOnUiThread { recyclerView.adapter?.notifyDataSetChanged() }
        }
        return true
    }
}