package com.example.l4z1

import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PhotosAdapter(private val data: ArrayList<Photo>, private val photosFragment: PhotosFragment) : RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.imageView)

        init {
            // gdy klikniemy na jakieś zdjęcie w galerii komunikacja
            view.setOnClickListener { //poprzez intencję do drugiej aktywności
                if (photosFragment.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    val intent = Intent(view.context, ShowPhotoActivity::class.java)
                    intent.putExtra("position", layoutPosition)
                    intent.putExtra("name", data[layoutPosition].name)
                    intent.putExtra("description", data[layoutPosition].description)
                    intent.putExtra("rating", data[layoutPosition].rating)
                    photosFragment.startActivityForResult(intent, 222)
                } else { // poprzez bezpośrednią komunikację z drugim fragmentem
                    val frag = photosFragment.fragmentManager!!.findFragmentById(R.id.showPhotoFragment) as ShowPhotoFragment
                    frag.show(layoutPosition, data[layoutPosition].name, data[layoutPosition].description, data[layoutPosition].rating)
                }
            }
            view.setOnLongClickListener {
                val builder = AlertDialog.Builder(view.context)
                builder.setTitle("WARNING")
                builder.setMessage("Are you sure you want to delete this photo?")
                builder.setPositiveButton("Yes") { _, _ ->
                    data.removeAt(layoutPosition)
                    notifyDataSetChanged()
                }
                builder.setNegativeButton("No") {_, _ -> }
                builder.show()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photos_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // rozmiar obrazów w zależności od położenia i rozmiaru ekranu
        var divider = 6
        if (photosFragment.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
            divider = 3
        holder.icon.layoutParams.height = Resources.getSystem().displayMetrics.widthPixels / divider
        val resId: Int = photosFragment.resources.getIdentifier(data[position].name, "drawable", holder.view.context.packageName)
        holder.icon.setImageResource(resId)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}