package com.example.l5z1

import android.content.Context
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM GameStatus ORDER BY ID DESC")
    fun getGames(): List<GameStatus>

    @Insert
    fun insert(gameStatus: GameStatus)

}

@Database(entities = [GameStatus::class], version = 1)
abstract class DatabaseConnector : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private var INSTANCE: DatabaseConnector? = null
        private var taskDao: TaskDao? = null

        fun getDatabaseConnector(context: Context) {
            if (INSTANCE == null) {
                synchronized(DatabaseConnector::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext, DatabaseConnector::class.java, "Game").build()
                    taskDao = INSTANCE!!.taskDao()
                }
            }
        }

        fun getGames(): List<GameStatus>? {
            return taskDao?.getGames()
        }

        fun insertGame(gameStatus: GameStatus) {
            taskDao?.insert(gameStatus)
        }
    }
}