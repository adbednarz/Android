package com.example.l4z2

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PagerAdapter(private val context: Context, private val photosList: ArrayList<Photo>,
                   private val adapterOnClick: AdapterOnClick, val bitmapList: ArrayList<Bitmap>):
    RecyclerView.Adapter<PagerAdapter.PageHolder>() {

    inner class PageHolder(view: View): RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val textView: TextView = view.findViewById(R.id.textView)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val button: Button = view.findViewById<Button>(R.id.button)
        var pos: Int = 0
        var rating: Double = 0.0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageHolder =
            PageHolder(LayoutInflater.from(context).inflate(R.layout.pager_layout, parent, false))

    override fun onBindViewHolder(holder: PageHolder, position: Int) {
        holder.pos = position
        holder.rating = photosList[position].rating
        // przypadek, gdy jest to robione zdjęcie
        if (photosList[position].name?.take(6) == "bitmap") {
            val num = photosList[position].name?.substring(6)?.toInt()
            holder.imageView.setImageBitmap(bitmapList[num!!])
        } else {
            val resId: Int = context.resources.getIdentifier(photosList[position].name, "drawable", context.packageName)
            holder.imageView.setImageResource((resId))
        }
        holder.textView.text = photosList[position].description
        holder.ratingBar.rating = photosList[position].rating.toFloat()
        holder.ratingBar.onRatingBarChangeListener =
                RatingBar.OnRatingBarChangeListener { _, rating, fromUser ->
                    if (fromUser) {
                        holder.rating = rating.toDouble()
                    }
                }
        holder.button.setOnClickListener { adapterOnClick.onClick(holder.rating, holder.pos) }
    }

    override fun getItemCount(): Int = photosList.size

}