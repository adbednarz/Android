package com.example.l5z1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class StatisticsActivity : AppCompatActivity() {

    private var gameList = ArrayList<GameStatus>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        GlobalScope.launch {
            val gameStatus = DatabaseConnector.getGames()
            if (gameStatus != null) {
                for (game in gameStatus)
                gameList.add(game)
            }
            recyclerView.adapter = StatisticsAdapter(gameList)
        }
    }
}