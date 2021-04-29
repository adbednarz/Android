package com.example.l4z2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

interface AdapterOnClick {
    fun onClick(item: Any, item2: Any)
}

class MainActivity : AppCompatActivity(), AdapterOnClick {

    private var photosList = ArrayList<Photo>()
    private var bitmapList = ArrayList<Bitmap>()
    private lateinit var descriptions: Array<String>
    private lateinit var pager: ViewPager2
    private val cameraRequest = 1888

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // sprawdzenie pozwolenia na użycie aparatu przez aplikację
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequest)

        descriptions = resources.getStringArray(R.array.descriptions)
        val scrollView: HorizontalScrollView = findViewById(R.id.scrollView)
        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val cameraButton: FloatingActionButton = findViewById(R.id.fab)

        cameraButton.setOnClickListener { _ ->
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, cameraRequest)
        }

        // przygotowuje listę zdjęć
        for (i in 0..19) {
            var icon: String = if (i < 11)
                "kot$i"
            else
                "pies$i"
            photosList.add(Photo(icon, descriptions[i], 0.0))
        }

        if (savedInstanceState != null)
            photosList = savedInstanceState.getParcelableArrayList("photos")!!

        pager = findViewById(R.id.pager)
        pager.adapter = PagerAdapter(this, photosList, this, bitmapList)

        // tworzy miniatury zdjęć
        TabLayoutMediator(tabLayout, pager) {tab, position ->
            val image = ImageView(this)
            // przypadek gdy jest to zdjęcie zrobione kamerą
            if (photosList[position].name?.take(6) == "bitmap") {
                val num = photosList[position].name?.substring(6)?.toInt()
                image.setImageBitmap(bitmapList[num!!])
            } else {
                val resId: Int = resources.getIdentifier(photosList[position].name, "drawable", packageName)
                image.setImageResource(resId)
            }
            tab.customView = image
            tab.customView?.layoutParams?.height = 250
            tab.customView?.layoutParams?.width = 250
        }.attach()

        // nadpisuje funkcję wywoływaną przy przesuwaniu, dzięki temu pasek też się przesuwa
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position > 1 && tab.position < photosList.size - 2) {
                    val divider = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT)
                        4
                    else
                        7
                    val scrollX = Resources.getSystem().displayMetrics.widthPixels / divider * tab.position
                    scrollView.smoothScrollTo(scrollX, scrollView.scrollY)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    // gdy kamera przesyła zdjęcie
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequest) {
            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            val result = Bitmap.createScaledBitmap(photo, 600, 600, false)
            bitmapList.add(result)
            photosList.add(Photo("bitmap${bitmapList.size-1}", descriptions[photosList.size], 0.0))
            pager.adapter?.notifyDataSetChanged()
        }
    }

    // gdy przekręcamy telefon zapisuje stan
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("photos", photosList)
    }

    // jest wywoływana przez przycisk w adepterze
    override fun onClick(item: Any, item2: Any) {
        println(item2)
        photosList[item2 as Int].rating = item as Double
        sort()
        pager.adapter?.notifyDataSetChanged()
    }

    // układa zdjęcia względem priorytetów
    private fun sort() {
        val sortedList = photosList.sortedWith(compareBy { it.rating })
        var i = photosList.size-1
        for (photo in sortedList) {
            photosList[i] = photo
            i--
        }
    }
}