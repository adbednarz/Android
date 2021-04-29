package com.example.l4z1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import androidx.core.content.getSystemService
import androidx.core.view.get
import androidx.fragment.app.Fragment


class ShowPhotoFragment : Fragment() {
    private var position = 0
    private var rating = 0.0
    private var name: String? = ""
    private var description: String? = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_show_photo, container, false)
        view.findViewById<Button>(R.id.button).setOnClickListener { onClick(it) }
        return view
    }

    // odebranie intencji w przypadku orientacji portretowej
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity?.intent != null && resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val pos = activity!!.intent.getIntExtra("position", 0)
            val name = activity!!.intent.getStringExtra("name")
            val desc = activity!!.intent.getStringExtra("description")
            val rat = activity!!.intent.getDoubleExtra("rating", 0.0)
            show(pos, name, desc, rat)
        }
    }

    // pokazywanie aktualnie wybranej pozycji z galerii
    fun show(pos: Int, name: String?, desc: String?, rat: Double) {
        position = pos
        this.name = name
        description = desc
        rating = rat

        val image = view!!.findViewById<ImageView>(R.id.imageView2)
        val resId: Int = resources.getIdentifier(name, "drawable", view!!.context.packageName)
        image.setImageResource(resId)

        val description = view!!.findViewById<TextView>(R.id.description)
        description.text = desc

        val ratingBar: RatingBar = view!!.findViewById(R.id.ratingBar)
        ratingBar.rating = rat.toFloat()
        ratingBar.onRatingBarChangeListener =
                OnRatingBarChangeListener { _, rating, fromUser ->
                    if (fromUser) {
                        this.rating = rating.toDouble()
                    }
                }
    }

    // zmiana właściwości danego obrazu
    private fun onClick(view: View) {
        // komunikacja poprzez intencję albo bezpośrednie odwołanie do fragmentu
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            var intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("rating", rating)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        } else {
            val frag = fragmentManager!!.findFragmentById(R.id.mainFragment) as PhotosFragment
            frag.change(position, rating)
        }
    }
}