package com.example.l4z1

import android.content.Intent
import android.content.res.Configuration
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PhotosFragment : Fragment() {
    private var photosList = ArrayList<Photo>()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val descriptions = resources.getStringArray(R.array.descriptions)
        // tworzenie zdjęć i dodanie ich do wspólnej listy
        for (i in 0..19) {
            var icon: String = if (i < 11)
                "kot$i"
            else
                "pies$i"
            photosList.add(Photo(icon, descriptions[i], 0.0))
        }

        // zapamiętywanie stanu po zmianie położenia
        if (savedInstanceState != null) {
            photosList = savedInstanceState.getParcelableArrayList("photos")!!
            // na początku po prawej stronie zawsze pokaże pierwszy obrazek, gdy nastąpi obrót
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                val frag = fragmentManager!!.findFragmentById(R.id.showPhotoFragment) as ShowPhotoFragment
                frag.show(0, photosList[0].name, photosList[0].description, photosList[0].rating)
            }
        }
        // tworzenie adaptera
        recyclerView = activity!!.findViewById(R.id.recyclerView)
        recyclerView?.layoutManager = GridLayoutManager(activity, 3)
        recyclerView?.adapter = PhotosAdapter(photosList, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 222) {
            val position = data?.getIntExtra("position", 0)
            val rating = data?.getDoubleExtra("rating", 0.0)
            if (position != null && rating != null)
                change(position, rating)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("photos", photosList)
    }

    fun change(position: Int, rating: Double) {
        photosList[position].rating = rating
        sort()
        recyclerView.adapter?.notifyDataSetChanged()
    }

    // sortowanie względem liczby gwiazdek
    private fun sort() {
        val sortedList = photosList.sortedWith(compareBy { it.rating })
        var i = photosList.size-1
        for (photo in sortedList) {
            photosList[i] = photo
            i--
        }
    }
}