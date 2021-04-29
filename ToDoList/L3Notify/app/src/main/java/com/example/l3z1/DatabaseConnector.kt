package com.example.l3z1

import android.content.Context
import android.view.MenuItem
import androidx.room.*

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task")
    fun getTasksByAddingOrder(): List<Task>

    @Query("SELECT * FROM Task ORDER BY name DESC")
    fun getTasksByAddingOrderDesc(): List<Task>

    @Query("SELECT * FROM Task ORDER BY name")
    fun getTasksByName(): List<Task>

    @Query("SELECT * FROM Task ORDER BY name DESC")
    fun getTasksByNameDesc(): List<Task>

    @Query("SELECT * FROM Task ORDER BY icon")
    fun getTasksByIcon(): List<Task>

    @Query("SELECT * FROM Task ORDER BY icon DESC")
    fun getTasksByIconDesc(): List<Task>

    @Query("SELECT * FROM Task ORDER BY date")
    fun getTasksByDate(): List<Task>

    @Query("SELECT * FROM Task ORDER BY date DESC")
    fun getTasksByDateDesc(): List<Task>

    @Query("SELECT * FROM Task WHERE addingOrder = :id")
    fun getSpecificTask(id: Int): Task

    @Query("SELECT * FROM Task WHERE date > :dateStart AND date < :dateEnd")
    fun getApproachingTasks(dateStart: String, dateEnd: String): List<Task>

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task?)

    @Delete
    fun delete(task: Task)
}

@Database(entities = [Task::class], version = 1)
abstract class DatabaseConnector : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        var INSTANCE: DatabaseConnector? = null
        var taskDao: TaskDao? = null

        fun getDatabaseConnector(context: Context) {
            if (INSTANCE == null) {
                synchronized(DatabaseConnector::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, DatabaseConnector::class.java, "Task").build()
                    taskDao = INSTANCE!!.taskDao()
                }
            }
        }

        fun getAll(): List<Task>? {
            return taskDao?.getTasksByAddingOrder()
        }

        fun getSpecific(id: Int): Task? {
            return taskDao?.getSpecificTask(id)
        }

        fun getApproaching(dateStart: String, dateEnd: String): List<Task>? {
            return taskDao?.getApproachingTasks(dateStart, dateEnd)
        }

        fun insertTask(task: Task) {
            taskDao?.insert(task)
        }

        fun updateTask(task: Task?) {
            taskDao?.update(task)
        }

        fun deleteTask(task: Task) {
            taskDao?.delete(task)
        }

        fun getSorted(sort: MenuItem): List<Task>? {
            return when (sort.itemId) {
                R.id.sortAddingOrderAsc -> {
                    taskDao?.getTasksByAddingOrder()
                }
                R.id.sortAddingOrderDesc -> {
                    taskDao?.getTasksByAddingOrderDesc()
                }
                R.id.sortNameAsc -> {
                    taskDao?.getTasksByName()
                }
                R.id.sortNameDesc -> {
                    taskDao?.getTasksByNameDesc()
                }
                R.id.sortIconAsc -> {
                    taskDao?.getTasksByIcon()
                }
                R.id.sortIconDesc -> {
                    taskDao?.getTasksByIconDesc()
                }
                R.id.sortDateAsc -> {
                    taskDao?.getTasksByDate()
                }
                R.id.sortDateDesc -> {
                    taskDao?.getTasksByDateDesc()
                }
                else -> null
            }
        }
        fun destroyDatabaseConnector() {
            INSTANCE = null
        }
    }
}