package com.example.l5z1

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StatisticsAdapter(private val data: ArrayList<GameStatus>) : RecyclerView.Adapter<StatisticsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val leftPaddle: TextView = view.findViewById(R.id.leftPaddle)
        val leftPoints: TextView = view.findViewById(R.id.leftPoints)
        val rightPaddle: TextView = view.findViewById(R.id.rightPaddle)
        val rightPoints: TextView = view.findViewById(R.id.rightPoints)
    }

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_statistics_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.leftPaddle.text = "leftPaddle"
        holder.leftPoints.text = data[position].leftPoints.toString()
        holder.rightPaddle.text = "rightPaddle"
        holder.rightPoints.text = data[position].rightPoints.toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }
}