package com.example.l5z1

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DatabaseConnector.getDatabaseConnector(this)
    }

    fun onClickStatistics(view : View) {
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent);
    }

    fun onClickPlay(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent);
    }
}